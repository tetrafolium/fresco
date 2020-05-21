/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.imagepipeline;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.imagepipeline.widget.ResizableFrameLayout;
import com.facebook.fresco.samples.showcase.misc.ImageUriProvider;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.platform.PlatformDecoder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/** Simple region decoding example that renders the original image and a selected region. */
public class ImagePipelineRegionDecodingFragment extends BaseShowcaseFragment {

  private SimpleDraweeView mFullDraweeView;
  private ResizableFrameLayout mSelectedRegion;
  private SimpleDraweeView mRegionDraweeView;
  private Uri mUri;
  private @Nullable ImageInfo mImageInfo;

  private final ControllerListener<ImageInfo> mControllerListener =
      new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(
            final String id,
            final @javax.annotation.Nullable ImageInfo imageInfo,
            final @javax.annotation.Nullable Animatable animatable) {
          mImageInfo = imageInfo;
          mSelectedRegion.setUpdateMaximumDimensionOnNextSizeChange(true);
          if (imageInfo != null) {
            mFullDraweeView.setAspectRatio(imageInfo.getWidth() / (float) imageInfo.getHeight());
            mFullDraweeView.requestLayout();
            updateRegion();
          }
        }
      };

  private final ResizableFrameLayout.SizeChangedListener mSizeChangedListener =
      new ResizableFrameLayout.SizeChangedListener() {
        @Override
        public void onSizeChanged(final int widthPx, final int heightPx) {
          updateRegion();
        }
      };

  @Nullable
  @Override
  public View onCreateView(
      final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_imagepipeline_region_decoding, container, false);
  }

  @Override
  public void onViewCreated(final View view, final @Nullable Bundle savedInstanceState) {
    mUri =
        sampleUris()
            .createSampleUri(ImageUriProvider.ImageSize.L, ImageUriProvider.Orientation.LANDSCAPE);

    mFullDraweeView = (SimpleDraweeView) view.findViewById(R.id.drawee_view_full);
    mFullDraweeView.setController(
        Fresco.newDraweeControllerBuilder()
            .setUri(mUri)
            .setControllerListener(mControllerListener)
            .build());

    mSelectedRegion = (ResizableFrameLayout) view.findViewById(R.id.frame_main);
    mSelectedRegion.init(view.findViewById(R.id.btn_resize));
    mSelectedRegion.setSizeChangedListener(mSizeChangedListener);

    mRegionDraweeView = (SimpleDraweeView) view.findViewById(R.id.drawee_view_region);
    mRegionDraweeView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
            updateRegion();
          }
        });
  }

  private void updateRegion() {
    if (mImageInfo == null) {
      return;
    }
    int left = 0;
    int top = 0;
    int right =
        mSelectedRegion.getMeasuredWidth()
            * mImageInfo.getWidth()
            / mFullDraweeView.getMeasuredWidth();
    int bottom =
        mSelectedRegion.getMeasuredHeight()
            * mImageInfo.getHeight()
            / mFullDraweeView.getMeasuredHeight();

    ImageDecoder regionDecoder = createRegionDecoder(left, top, right, bottom);
    mRegionDraweeView.setController(
        Fresco.newDraweeControllerBuilder()
            .setImageRequest(
                ImageRequestBuilder.newBuilderWithSource(mUri)
                    .setImageDecodeOptions(
                        ImageDecodeOptions.newBuilder()
                            .setCustomImageDecoder(regionDecoder)
                            .build())
                    .build())
            .build());
  }

  @Override
  public int getTitleId() {
    return R.string.imagepipeline_region_decoding_title;
  }

  private ImageDecoder createRegionDecoder(final int left, final int top, final int right, final int bottom) {
    return new RegionDecoder(
        Fresco.getImagePipelineFactory().getPlatformDecoder(), new Rect(left, top, right, bottom));
  }

  public static class RegionDecoder implements ImageDecoder {

    private final PlatformDecoder mPlatformDecoder;
    private final Rect mRegion;

    public RegionDecoder(final PlatformDecoder platformDecoder, final Rect region) {
      mPlatformDecoder = platformDecoder;
      mRegion = region;
    }

    @Override
    public CloseableImage decode(
        final EncodedImage encodedImage,
        final int length,
        final QualityInfo qualityInfo,
        final ImageDecodeOptions options) {
      CloseableReference<Bitmap> decodedBitmapReference =
          mPlatformDecoder.decodeJPEGFromEncodedImageWithColorSpace(
              encodedImage, options.bitmapConfig, mRegion, length, options.colorSpace);
      try {
        return new CloseableStaticBitmap(decodedBitmapReference, qualityInfo, 0);
      } finally {
        CloseableReference.closeSafely(decodedBitmapReference);
      }
    }
  }
}
