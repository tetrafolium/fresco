/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.vito.core.impl;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.util.ObjectsCompat;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.info.ImageOrigin;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.drawable.InstrumentedDrawable;
import com.facebook.fresco.middleware.MiddlewareUtils;
import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.fresco.ui.common.ControllerListener2.Extras;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.fresco.vito.core.FrescoContext;
import com.facebook.fresco.vito.core.FrescoController;
import com.facebook.fresco.vito.core.FrescoExperiments;
import com.facebook.fresco.vito.core.FrescoState;
import com.facebook.fresco.vito.core.Hierarcher;
import com.facebook.fresco.vito.core.VitoUtils;
import com.facebook.fresco.vito.core.impl.debug.DebugOverlayFactory;
import com.facebook.fresco.vito.listener.ImageListener;
import com.facebook.fresco.vito.listener.impl.AutoPlayImageListener;
import com.facebook.fresco.vito.listener.impl.ForwardingImageListener;
import com.facebook.fresco.vito.options.ImageOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.HasImageMetadata;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.OriginalEncodedImageInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.multiuri.MultiUri;
import com.facebook.imagepipeline.producers.InternalProducerListener;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Map;
import javax.annotation.Nullable;

public class FrescoControllerImpl implements FrescoController {

  private static final Map<String, Object> COMPONENT_EXTRAS =
      ImmutableMap.<String, Object>of("component_tag", "vito1");
  private static final Map<String, Object> SHORTCUT_EXTRAS =
      ImmutableMap.<String, Object>of("origin", "memory_bitmap", "origin_sub", "shortcut");

  private final FrescoContext mFrescoContext;
  private final DebugOverlayFactory mDebugOverlayFactory;
  private final boolean mShouldInstrumentDrawable;
  private final ControllerListener2<ImageInfo> mControllerListener2;

  public FrescoControllerImpl(
      final FrescoContext frescoContext,
      final DebugOverlayFactory debugOverlayFactory,
      final boolean shouldInstrumentDrawable,
      final @Nullable ControllerListener2<ImageInfo> controllerListener2) {
    mFrescoContext = frescoContext;
    mDebugOverlayFactory = debugOverlayFactory;
    mShouldInstrumentDrawable = shouldInstrumentDrawable;
    mControllerListener2 = controllerListener2;
  }

  @Override
  public FrescoState onPrepare(
      final FrescoState frescoState,
      final @Nullable Uri uri,
      final @Nullable MultiUri multiUri,
      final ImageOptions imageOptions,
      final @Nullable Object callerContext,
      final Resources resources,
      final @Nullable ImageListener imageListener) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#onPrepare");
    }
    try {
      final boolean stateHasSameProps =
          frescoState != null
              && ObjectsCompat.equals(uri, frescoState.getUri())
              && ObjectsCompat.equals(multiUri, frescoState.getMultiUri())
              && ObjectsCompat.equals(imageOptions, frescoState.getImageOptions())
              && (!(mFrescoContext.getExperiments().shouldDiffCallerContext()) || ObjectsCompat.equals(callerContext, frescoState.getCallerContext()));
      // If state is recycled from another component we must create a new one
      return stateHasSameProps
          ? frescoState
          : createState(uri, multiUri, imageOptions, callerContext, resources, imageListener);
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  @Override
  public FrescoState createState(
      final @Nullable Uri uri,
      final @Nullable MultiUri multiUri,
      final ImageOptions imageOptions,
      final @Nullable Object callerContext,
      final Resources resources,
      final @Nullable ImageListener imageListener) {
    mFrescoContext.verifyCallerContext(callerContext);
    validateUris(uri, multiUri);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#createState");
    }
    try {
      final FrescoExperiments frescoExperiments = mFrescoContext.getExperiments();

      final ImageRequest imageRequest =
          mFrescoContext.getImagePipelineUtils().buildImageRequest(uri, imageOptions);
      final CacheKey cacheKey =
          mFrescoContext.getImagePipeline().getCacheKey(imageRequest, callerContext);
      CloseableReference<CloseableImage> cachedImage = null;
      boolean isImageCached;
      if (frescoExperiments.cacheImageInState()) {
        cachedImage = getCachedImage(cacheKey);
        isImageCached = cachedImage != null;
      } else {
        isImageCached = mFrescoContext.getImagePipeline().hasCachedImage(cacheKey);
      }
      final FrescoState frescoState =
          new FrescoStateImpl(
              VitoUtils.generateIdentifier(),
              mFrescoContext,
              uri,
              multiUri,
              imageOptions,
              callerContext,
              imageRequest,
              cacheKey,
              cachedImage,
              resources,
              imageListener,
              new ForwardingImageListener(
                  mFrescoContext.getGlobalImageListener(),
                  imageOptions.shouldAutoPlay() ? AutoPlayImageListener.getInstance() : null),
              mFrescoContext.getGlobalImageStateListener());
      if (frescoExperiments.prepareActualImageWrapperInBackground()) {
        prepareActualImageInBackground(frescoState);
      }

      if (!isImageCached) {
        if (frescoExperiments.prepareImagePipelineComponents() && imageRequest != null) {
          prepareImagePipelineComponents(frescoState, imageRequest, callerContext);
        }

        if (frescoExperiments.prefetchInOnPrepare()) {
          Runnable runnable =
              new Runnable() {
                @Override
                public void run() {
                  if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection(
                        "FrescoControllerImpl#createState->prefetchInOnPrepare");
                  }
                  try {
                    if (frescoExperiments.useFetchApiForPrefetch()) {
                      DataSource<CloseableReference<CloseableImage>> dataSource =
                          fireOffRequest(frescoState);
                      dataSource.subscribe(
                          frescoState, mFrescoContext.getUiThreadExecutorService());
                      if (frescoExperiments.keepRefToPrefetchDatasource()) {
                        frescoState.setPrefetchDatasource(dataSource);
                      }
                    } else {
                      DataSource<Void> dataSource =
                          mFrescoContext
                              .getPrefetcher()
                              .prefetch(
                                  frescoExperiments.onPreparePrefetchTarget(),
                                  uri,
                                  imageOptions,
                                  callerContext);
                      if (frescoExperiments.keepRefToPrefetchDatasource()) {
                        frescoState.setPrefetchDatasource(dataSource);
                      }
                    }
                  } finally {
                    if (FrescoSystrace.isTracing()) {
                      FrescoSystrace.endSection();
                    }
                  }
                }
              };
          if (mFrescoContext.getExperiments().enqueuePrefetchInOnPrepare()
              && mFrescoContext.getLightweightBackgroundThreadExecutor() != null) {
            mFrescoContext.getLightweightBackgroundThreadExecutor().execute(runnable);
          } else {
            runnable.run();
          }
        }

        if (frescoExperiments.preparePlaceholderDrawableInBackground()) {
          final Drawable placeholderDrawable =
              mFrescoContext
                  .getHierarcher()
                  .buildPlaceholderDrawable(
                      frescoState.getResources(), frescoState.getImageOptions());
          frescoState.setPlaceholderDrawable(placeholderDrawable);
        }
      }
      return frescoState;
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  private void validateUris(final @Nullable Uri uri, final @Nullable MultiUri multiUri) {
    Preconditions.checkState(
        (uri == null) || (multiUri == null), "Cannot specify both uri and multiUri props!");
  }

  private void prepareActualImageInBackground(final FrescoState frescoState) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#prepareActualImageInBackground");
    }
    Hierarcher hierarcher = mFrescoContext.getHierarcher();
    frescoState.setActualImageWrapper(
        hierarcher.buildActualImageWrapper(frescoState.getImageOptions()));
    frescoState.setOverlayDrawable(
        hierarcher.buildOverlayDrawable(frescoState.getResources(), frescoState.getImageOptions()));
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }

  /** Creates imagepipeline components and sets them to frescoState */
  @VisibleForTesting
  void prepareImagePipelineComponents(
      final FrescoState frescoState, final ImageRequest imageRequest, final @Nullable Object callerContext) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#prepareImagePipelineComponents");
    }
    try {
      frescoState.setProducerSequence(
          mFrescoContext
              .getImagePipeline()
              .getProducerSequenceFactory()
              .getDecodedImageProducerSequence(imageRequest));

      ImageRequest.RequestLevel lowestPermittedRequestLevel =
          ImageRequest.RequestLevel.getMax(
              imageRequest.getLowestPermittedRequestLevel(), ImageRequest.RequestLevel.FULL_FETCH);

      setupRequestListener(frescoState, imageRequest);
      frescoState.setSettableProducerContext(
          new SettableProducerContext(
              imageRequest,
              mFrescoContext.getImagePipeline().generateUniqueFutureId(),
              frescoState.getStringId(),
              new InternalProducerListener(frescoState.getRequestListener(), null),
              callerContext,
              lowestPermittedRequestLevel,
              /* isPrefetch */ false,
              imageRequest.getProgressiveRenderingEnabled()
                  || !UriUtil.isNetworkUri(imageRequest.getSourceUri()),
              imageRequest.getPriority(),
              mFrescoContext.getImagePipeline().getConfig()));
    } catch (Exception exception) {
      // This is how ImagePipeline handles the error case.
      // Something went wrong and we can't prepare components ahead of time.
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  @Override
  public void onAttach(final FrescoState frescoState, final @Nullable ImageListener imageListener) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#onAttach");
    }
    try {
      final FrescoExperiments experiments = mFrescoContext.getExperiments();
      if (experiments.closeDatasource()) {
        DeferredReleaser.getInstance().cancelDeferredRelease(frescoState);
      }

      frescoState.setAttached(true);
      frescoState.setImageListener(imageListener);

      final ImageRequest imageRequest = frescoState.getImageRequest();
      if (frescoState.getImageOptions().shouldResizeToViewport()
          && frescoState.getTargetWidthPx() > 0
          && frescoState.getTargetHeightPx() > 0
          && imageRequest != null
          && imageRequest.getResizeOptions() == null) {
        frescoState.setImageRequest(
            ImageRequestBuilder.fromRequest(imageRequest)
                .setResizeOptions(
                    ResizeOptions.forDimensions(
                        frescoState.getTargetWidthPx(), frescoState.getTargetHeightPx()))
                .setResizingAllowedOverride(true)
                .build());
      }

      if (!frescoState.getFrescoDrawable().isDefaultLayerIsOn()) {
        frescoState.getFrescoDrawable().reset();
      }

      mFrescoContext
          .getHierarcher()
          .setupOverlayDrawable(
              frescoState.getFrescoDrawable(),
              frescoState.getResources(),
              frescoState.getImageOptions(),
              frescoState.getOverlayDrawable());

      if (mControllerListener2 != null) {
        mControllerListener2.onSubmit(
            VitoUtils.getStringId(frescoState.getId()), frescoState.getCallerContext());
      }
      frescoState.onSubmit(frescoState.getId(), frescoState.getCallerContext());

      // Check if we have a cached image in the state
      CloseableReference<CloseableImage> cachedImage = null;
      if (experiments.checkStateCacheInAttach()) {
        cachedImage = frescoState.getCachedImage();
        try {
          if (CloseableReference.isValid(cachedImage)) {
            frescoState.setImageOrigin(ImageOrigin.MEMORY_BITMAP_SHORTCUT);
            displayResultOrError(frescoState, cachedImage, true, null);
            return;
          }
        } finally {
          CloseableReference.closeSafely(cachedImage);
        }
      }

      // Check if the image is in cache now
      if (experiments.checkCacheInAttach()) {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("FrescoControllerImpl#onAttach->getCachedImage");
        }
        try {
          try {
            cachedImage = getCachedImage(frescoState.getCacheKey());
          } finally {
            if (FrescoSystrace.isTracing()) {
              FrescoSystrace.endSection();
            }
          }
          if (CloseableReference.isValid(cachedImage)) {
            if (experiments.cacheImageInState()) {
              frescoState.setCachedImage(cachedImage);
            }
            frescoState.setImageOrigin(ImageOrigin.MEMORY_BITMAP_SHORTCUT);
            displayResultOrError(frescoState, cachedImage, true, null);
            return;
          }
        } finally {
          CloseableReference.closeSafely(cachedImage);
        }
      }

      // Not in cache, set placeholder and fetch image
      final Drawable placeholderDrawable;
      if (frescoState.getPlaceholderDrawable() != null) {
        placeholderDrawable = frescoState.getPlaceholderDrawable();
      } else {
        placeholderDrawable =
            mFrescoContext
                .getHierarcher()
                .buildPlaceholderDrawable(
                    frescoState.getResources(), frescoState.getImageOptions());
      }
      frescoState.getFrescoDrawable().setPlaceholderDrawable(placeholderDrawable);

      final Drawable progressDrawable =
          mFrescoContext
              .getHierarcher()
              .buildProgressDrawable(frescoState.getResources(), frescoState.getImageOptions());
      frescoState.getFrescoDrawable().setProgressDrawable(progressDrawable);

      if (frescoState.getImageRequest() != null || frescoState.getMultiUri() != null) {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("FrescoControllerImpl#onAttach->fetch");
        }
        try {
          if (mFrescoContext.getExperiments().fireOffRequestInBackground()
              && mFrescoContext.getLightweightBackgroundThreadExecutor() != null) {
            Runnable fetchRunnable =
                new Runnable() {
                  @Override
                  public void run() {
                    // If the runnable is executed after the image has been detached, we don't do
                    // anything.
                    if (!frescoState.isAttached()) {
                      return;
                    }
                    if (FrescoSystrace.isTracing()) {
                      FrescoSystrace.beginSection("FrescoControllerImpl#onAttach->fetchRunnable");
                    }
                    try {
                      DataSource<CloseableReference<CloseableImage>> dataSource =
                          createDataSource(frescoState);
                      dataSource.subscribe(
                          frescoState, mFrescoContext.getUiThreadExecutorService());
                      if (experiments.keepRefToMainFetchDatasource()) {
                        frescoState.setMainFetchDatasource(dataSource);
                      }
                    } finally {
                      if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                      }
                    }
                  }
                };
            mFrescoContext.getLightweightBackgroundThreadExecutor().execute(fetchRunnable);
          } else {
            DataSource<CloseableReference<CloseableImage>> dataSource =
                createDataSource(frescoState);
            dataSource.subscribe(frescoState, mFrescoContext.getUiThreadExecutorService());
            if (experiments.keepRefToMainFetchDatasource()) {
              frescoState.setMainFetchDatasource(dataSource);
            }
          }
        } finally {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
        }
      }
    } finally {
      if (mFrescoContext.getExperiments().closePrefetchDataSource())
        // In any case, we can now cancel the prefetch
        frescoState.setPrefetchDatasource(null);
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  private DataSource<CloseableReference<CloseableImage>> createDataSource(final FrescoState frescoState) {
    if (frescoState.getUri() != null) {
      return fireOffRequest(frescoState);
    } else {
      return MultiUri.getMultiUriDatasourceSupplier(
              mFrescoContext.getImagePipeline(),
              frescoState.getMultiUri(),
              frescoState.getImageRequest(),
              frescoState.getCallerContext(),
              frescoState.getRequestListener(),
              frescoState.getStringId())
          .get();
    }
  }

  protected DataSource<CloseableReference<CloseableImage>> fireOffRequest(final FrescoState frescoState) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#fireOffRequest");
    }
    DataSource<CloseableReference<CloseableImage>> dataSource;
    if (mFrescoContext.getExperiments().prepareImagePipelineComponents()
        && frescoState.getProducerSequence() != null
        && frescoState.getSettableProducerContext() != null) {
      dataSource =
          mFrescoContext
              .getImagePipeline()
              .submitFetchRequest(
                  frescoState.getProducerSequence(),
                  frescoState.getSettableProducerContext(),
                  frescoState.getRequestListener());
    } else {
      setupRequestListener(frescoState, frescoState.getImageRequest());
      dataSource =
          MultiUri.getImageRequestDataSource(
              mFrescoContext.getImagePipeline(),
              frescoState.getImageRequest(),
              frescoState.getCallerContext(),
              frescoState.getRequestListener(),
              frescoState.getStringId());
    }
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return dataSource;
  }

  @Override
  public void onDetach(final FrescoState frescoState) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#onDetach");
    }
    frescoState.setAttached(false);

    if (mFrescoContext.getExperiments().closeDatasource()) {
      DeferredReleaser.getInstance().scheduleDeferredRelease(frescoState);
    }

    if (frescoState.getFrescoDrawable() != null) {
      frescoState.getFrescoDrawable().close();
      frescoState.setFrescoDrawable(null);
    }
    CloseableReference.closeSafely(frescoState.getCachedImage());

    frescoState.onRelease(frescoState.getId());
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }

  @Override
  public void onNewResult(
      final FrescoState frescoState, final DataSource<CloseableReference<CloseableImage>> dataSource) {
    if (dataSource != null && !dataSource.isClosed()) {
      final boolean shouldClose =
          mFrescoContext.getExperiments().closeDatasourceOnNewResult() && dataSource.isFinished();
      final CloseableReference<CloseableImage> result = dataSource.getResult();
      try {
        frescoState.setImageFetched(true);
        if (frescoState.isAttached()) {
          displayResultOrError(frescoState, result, false, dataSource);
        }
      } finally {
        CloseableReference.closeSafely(result);
        if (shouldClose) {
          dataSource.close();
        }
      }
    }
  }

  @Override
  public void onFailure(
      final FrescoState frescoState, final DataSource<CloseableReference<CloseableImage>> dataSource) {
    Drawable errorDrawable =
        mFrescoContext
            .getHierarcher()
            .buildErrorDrawable(frescoState.getResources(), frescoState.getImageOptions());
    displayErrorImage(frescoState, errorDrawable);
    frescoState.onFailure(frescoState.getId(), errorDrawable, dataSource.getFailureCause());
  }

  @Override
  public void onCancellation(
      final FrescoState frescoState, final DataSource<CloseableReference<CloseableImage>> dataSource) { }

  @Override
  public void onProgressUpdate(
      final FrescoState frescoState, final DataSource<CloseableReference<CloseableImage>> dataSource) { }

  @Nullable
  private CloseableReference<CloseableImage> getCachedImage(final @Nullable CacheKey cacheKey) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#getCachedImage");
    }
    try {
      CloseableReference<CloseableImage> cachedImageReference =
          mFrescoContext.getImagePipeline().getCachedImage(cacheKey);
      if (CloseableReference.isValid(cachedImageReference)) {
        return cachedImageReference;
      }
      return null;
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  @VisibleForTesting
  void displayErrorImage(final FrescoState frescoState, final @Nullable Drawable errorDrawable) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#displayErrorImage");
    }
    try {
      if (!frescoState.isAttached()) {
        return;
      }
      frescoState.getFrescoDrawable().setProgressDrawable(null);

      if (errorDrawable != null) {
        frescoState.getFrescoDrawable().setImageDrawable(errorDrawable);
      }
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  @VisibleForTesting
  void displayResultOrError(
      final FrescoState frescoState,
      final @Nullable CloseableReference<CloseableImage> result,
      final boolean wasImmediate,
      final @Nullable DataSource<CloseableReference<CloseableImage>> dataSource) {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("FrescoControllerImpl#displayResultOrError");
    }
    try {
      if (!frescoState.isAttached()) {
        return;
      }

      CloseableImage closeableImage;
      if (result == null || (closeableImage = result.get()) == null) {
        Drawable errorDrawable =
            mFrescoContext
                .getHierarcher()
                .buildErrorDrawable(frescoState.getResources(), frescoState.getImageOptions());
        displayErrorImage(frescoState, errorDrawable);
        frescoState.onFailure(frescoState.getId(), errorDrawable, null);
        return;
      }

      frescoState.getFrescoDrawable().setProgressDrawable(null);

      InstrumentedDrawable.Listener instrumentedListener =
          maybeGetInstrumentedListener(frescoState, result);

      Drawable actualDrawable =
          mFrescoContext
              .getHierarcher()
              .setupActualImageDrawable(
                  frescoState.getFrescoDrawable(),
                  frescoState.getResources(),
                  frescoState.getImageOptions(),
                  result,
                  frescoState.getActualImageWrapper(),
                  wasImmediate,
                  instrumentedListener);

      if (mControllerListener2 != null) {
        mControllerListener2.onFinalImageSet(
            VitoUtils.getStringId(frescoState.getId()),
            closeableImage,
            obtainExtras(
                dataSource,
                closeableImage,
                frescoState.getFrescoDrawable().getViewportDimensions()));
      }
      frescoState.onFinalImageSet(
          frescoState.getId(), frescoState.getImageOrigin(), closeableImage, actualDrawable);

      mFrescoContext
          .getHierarcher()
          .setupDebugOverlayDrawable(
              frescoState.getFrescoDrawable(),
              frescoState.getFrescoDrawable().getOverlayDrawable(),
              mDebugOverlayFactory.create(frescoState));

    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  private @Nullable InstrumentedDrawable.Listener maybeGetInstrumentedListener(
      final FrescoState frescoState, final CloseableReference<CloseableImage> result) {
    InstrumentedDrawable.Listener instrumentedListener = null;
    if (mShouldInstrumentDrawable) {
      ImageInfo imageInfo = result.get();
      instrumentedListener = createListener(frescoState, imageInfo, frescoState.getStringId());
    }
    return instrumentedListener;
  }

  private void setupRequestListener(final FrescoState frescoState, final ImageRequest imageRequest) {
    // TODO(T35949558): Add support for external request listeners
    if (imageRequest == null) {
      return;
    }
    final RequestListener requestListener =
        mFrescoContext
            .getImagePipeline()
            .getRequestListenerForRequest(imageRequest, frescoState.getImageOriginListener());
    frescoState.setRequestListener(requestListener);
  }

  private InstrumentedDrawable.Listener createListener(
      final ImageListener imageListener, final ImageInfo info, final String id) {
    return new InstrumentedDrawable.Listener() {

      public static final String TAG = "InstrumentedDrawable.Listener";

      @Override
      public void track(
          final int viewWidth,
          final int viewHeight,
          final int imageWidth,
          final int imageHeight,
          final int scaledWidth,
          final int scaledHeight,
          final String scaleType) {
        if (imageListener != null) {
          if (!(info instanceof HasImageMetadata)) {
            FLog.wtf(TAG, "mInfo does not implement HasImageMetadata: " + info);
          } else {
            int encodedImageWidth = -1;
            int encodedImageHeight = -1;
            OriginalEncodedImageInfo encodedImageInfo =
                ((HasImageMetadata) info).getOriginalEncodedImageInfo();
            if (encodedImageInfo != null) {
              encodedImageWidth = encodedImageInfo.getWidth();
              encodedImageHeight = encodedImageInfo.getHeight();
            }
            DimensionsInfo dimensionsInfo =
                new DimensionsInfo(
                    viewWidth,
                    viewHeight,
                    encodedImageWidth,
                    encodedImageHeight,
                    imageWidth,
                    imageHeight,
                    scaleType);

            if (imageListener != null) {
              imageListener.onImageDrawn(id, info, dimensionsInfo);
            }
          }
        }
      }
    };
  }

  private static Extras obtainExtras(
      final @Nullable DataSource<CloseableReference<CloseableImage>> dataSource,
      final CloseableImage closeableImage,
      final @Nullable Rect viewportDimensions) {
    return MiddlewareUtils.obtainExtras(
        COMPONENT_EXTRAS,
        SHORTCUT_EXTRAS,
        dataSource,
        viewportDimensions,
        closeableImage.getAsExtras());
  }
}
