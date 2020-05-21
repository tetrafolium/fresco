/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.vito;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.misc.ImageUriProvider;
import com.facebook.fresco.vito.litho.FrescoVitoImage;
import com.facebook.fresco.vito.options.ImageOptions;
import com.facebook.fresco.vito.options.RoundingOptions;
import com.facebook.imagepipeline.multiuri.MultiUri;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;

/** Simple experimental Fresco Vito fragment that just displays an image. */
public class FrescoVitoLithoSimpleFragment extends BaseShowcaseFragment {

  private static final ImageOptions IMAGE_OPTIONS =
      ImageOptions.create()
          .placeholderRes(R.drawable.logo)
          .round(RoundingOptions.asCircle())
          .build();

  @Nullable
  @Override
  public View onCreateView(
      final @Nullable LayoutInflater inflater,
      final @Nullable ViewGroup container,
      final @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_vito_simple, container, false);
  }

  @Override
  public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
    final ComponentContext componentContext = new ComponentContext(getContext());

    FrameLayout container = view.findViewById(R.id.container);
    container.addView(LithoView.create(componentContext, createComponent(componentContext)));
  }

  @Override
  public int getTitleId() {
    return R.string.vito_litho_simple;
  }

  public Component createComponent(final ComponentContext c) {
    Uri uri0 = Uri.parse("http://sample.com/invalid");
    Uri uri1 = sampleUris().createSampleUri(ImageUriProvider.ImageSize.XXL);
    Uri uri2 = Uri.parse("http://sample.com/invalid");

    return FrescoVitoImage.create(c)
        .multiUri(
            MultiUri.create()
                .setLowResImageRequest(ImageRequest.fromUri(uri0))
                .setImageRequests(
                    ImageRequest.fromUri(uri0),
                    ImageRequest.fromUri(uri1),
                    ImageRequest.fromUri(uri2))
                .build())
        .imageOptions(IMAGE_OPTIONS)
        .build();
  }
}
