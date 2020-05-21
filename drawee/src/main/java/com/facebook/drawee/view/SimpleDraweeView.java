/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.drawee.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import androidx.annotation.DrawableRes;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.R;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.Nullable;

/**
 * This view takes a uri as input and internally builds and sets a controller.
 *
 * <p>This class must be statically initialized in order to be used. If you are using the Fresco
 * image pipeline, use {@link com.facebook.drawee.backends.pipeline.Fresco#initialize} to do this.
 */
public class SimpleDraweeView extends GenericDraweeView {

  private static Supplier<? extends AbstractDraweeControllerBuilder>
      sDraweecontrollerbuildersupplier;

  /** Initializes {@link SimpleDraweeView} with supplier of Drawee controller builders. */
  public static void initialize(
      final Supplier<? extends AbstractDraweeControllerBuilder> draweeControllerBuilderSupplier) {
    sDraweecontrollerbuildersupplier = draweeControllerBuilderSupplier;
  }

  /** Shuts {@link SimpleDraweeView} down. */
  public static void shutDown() {
    sDraweecontrollerbuildersupplier = null;
  }

  private AbstractDraweeControllerBuilder mControllerBuilder;

  public SimpleDraweeView(final Context context, final GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
    init(context, null);
  }

  public SimpleDraweeView(final Context context) {
    super(context);
    init(context, null);
  }

  public SimpleDraweeView(final Context context, final AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public SimpleDraweeView(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public SimpleDraweeView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(final Context context, final @Nullable AttributeSet attrs) {
    try {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("SimpleDraweeView#init");
      }
      if (isInEditMode()) {
        getTopLevelDrawable().setVisible(true, false);
        getTopLevelDrawable().invalidateSelf();
      } else {
        Preconditions.checkNotNull(
            sDraweecontrollerbuildersupplier, "SimpleDraweeView was not initialized!");
        mControllerBuilder = sDraweecontrollerbuildersupplier.get();
      }

      if (attrs != null) {
        TypedArray gdhAttrs = context.obtainStyledAttributes(attrs, R.styleable.SimpleDraweeView);
        try {
          if (gdhAttrs.hasValue(R.styleable.SimpleDraweeView_actualImageUri)) {
            setImageURI(
                Uri.parse(gdhAttrs.getString(R.styleable.SimpleDraweeView_actualImageUri)), null);
          } else if (gdhAttrs.hasValue((R.styleable.SimpleDraweeView_actualImageResource))) {
            int resId =
                gdhAttrs.getResourceId(R.styleable.SimpleDraweeView_actualImageResource, NO_ID);
            if (resId != NO_ID) {
              if (isInEditMode()) {
                setImageResource(resId);
              } else {
                setActualImageResource(resId);
              }
            }
          }
        } finally {
          gdhAttrs.recycle();
        }
      }
    } finally {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }

  protected AbstractDraweeControllerBuilder getControllerBuilder() {
    return mControllerBuilder;
  }

  /**
   * Sets the image request
   *
   * @param request Image Request
   */
  public void setImageRequest(final ImageRequest request) {
    AbstractDraweeControllerBuilder controllerBuilder = mControllerBuilder;
    DraweeController controller =
        controllerBuilder.setImageRequest(request).setOldController(getController()).build();
    setController(controller);
  }

  /**
   * Displays an image given by the uri.
   *
   * @param uri uri of the image
   * @undeprecate
   */
  @Override
  public void setImageURI(final Uri uri) {
    setImageURI(uri, null);
  }

  /**
   * Displays an image given by the uri string.
   *
   * @param uriString uri string of the image
   */
  public void setImageURI(final @Nullable String uriString) {
    setImageURI(uriString, null);
  }

  /**
   * Displays an image given by the uri.
   *
   * @param uri uri of the image
   * @param callerContext caller context
   */
  public void setImageURI(final Uri uri, final @Nullable Object callerContext) {
    DraweeController controller =
        mControllerBuilder
            .setCallerContext(callerContext)
            .setUri(uri)
            .setOldController(getController())
            .build();
    setController(controller);
  }

  /**
   * Displays an image given by the uri string.
   *
   * @param uriString uri string of the image
   * @param callerContext caller context
   */
  public void setImageURI(final @Nullable String uriString, final @Nullable Object callerContext) {
    Uri uri = (uriString != null) ? Uri.parse(uriString) : null;
    setImageURI(uri, callerContext);
  }

  /**
   * Sets the actual image resource to the given resource ID.
   *
   * <p>Similar to {@link #setImageResource(int)}, this sets the displayed image to the given
   * resource. However, {@link #setImageResource(int)} bypasses all Drawee functionality and makes
   * the view act as a normal {@link android.widget.ImageView}, whereas this method keeps all of the
   * Drawee functionality, including the {@link com.facebook.drawee.interfaces.DraweeHierarchy}.
   *
   * @param resourceId the resource ID to use.
   */
  public void setActualImageResource(final @DrawableRes int resourceId) {
    setActualImageResource(resourceId, null);
  }

  /**
   * Sets the actual image resource to the given resource ID.
   *
   * <p>Similar to {@link #setImageResource(int)}, this sets the displayed image to the given
   * resource. However, {@link #setImageResource(int)} bypasses all Drawee functionality and makes
   * the view act as a normal {@link android.widget.ImageView}, whereas this method keeps all of the
   * Drawee functionality, including the {@link com.facebook.drawee.interfaces.DraweeHierarchy}.
   *
   * @param resourceId the resource ID to use.
   * @param callerContext caller context
   */
  public void setActualImageResource(final @DrawableRes int resourceId, final @Nullable Object callerContext) {
    setImageURI(UriUtil.getUriForResourceId(resourceId), callerContext);
  }

  /**
   * This method will bypass all Drawee-related functionality. If you want to keep this
   * functionality, take a look at {@link #setActualImageResource(int)} and {@link
   * #setActualImageResource(int, Object)}}.
   *
   * @param resId the resource ID
   */
  @Override
  public void setImageResource(final int resId) {
    super.setImageResource(resId);
  }
}
