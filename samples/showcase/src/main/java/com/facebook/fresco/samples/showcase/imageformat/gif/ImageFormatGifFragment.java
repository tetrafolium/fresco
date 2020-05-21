/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.imageformat.gif;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import com.facebook.animated.giflite.GifDecoder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.misc.CheckerBoardDrawable;
import com.facebook.fresco.samples.showcase.misc.ImageUriProvider;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/** GIF example that illustrates how to display a simple GIF file */
public class ImageFormatGifFragment extends BaseShowcaseFragment {

  private Entry[] mSpinnerEntries;

  private Spinner mSpinner;
  private SimpleDraweeView mSimpleDraweeView;
  private @Nullable GifDecoder mGifDecoder;

  @Nullable
  @Override
  public View onCreateView(
      final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_format_gif, container, false);
  }

  @Override
  public void onViewCreated(final View view, final @Nullable Bundle savedInstanceState) {

    mSpinnerEntries =
        new Entry[] {
          new Entry(
              R.string.format_gif_label_small,
              sampleUris().createGifUri(ImageUriProvider.ImageSize.S)),
          new Entry(
              R.string.format_gif_label_medium,
              sampleUris().createGifUri(ImageUriProvider.ImageSize.M)),
          new Entry(
              R.string.format_gif_label_large,
              sampleUris().createGifUri(ImageUriProvider.ImageSize.L)),
        };

    mSimpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.drawee_view);

    final SwitchCompat switchBackground = (SwitchCompat) view.findViewById(R.id.switch_background);
    switchBackground.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            mSimpleDraweeView
                .getHierarchy()
                .setBackgroundImage(isChecked ? new CheckerBoardDrawable(getResources()) : null);
          }
        });

    final SwitchCompat switchAspect = (SwitchCompat) view.findViewById(R.id.switch_aspect_ratio);
    switchAspect.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            ViewGroup.LayoutParams layoutParams = mSimpleDraweeView.getLayoutParams();
            layoutParams.height = layoutParams.width * (isChecked ? 2 : 1);
            mSimpleDraweeView.setLayoutParams(layoutParams);
          }
        });

    mSpinner = (Spinner) view.findViewById(R.id.spinner);
    mSpinner.setAdapter(new SimpleUriListAdapter());
    mSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
            refreshAnimation();
          }

          @Override
          public void onNothingSelected(final AdapterView<?> parent) { }
        });
    mSpinner.setSelection(0);

    final Spinner decoderSpinner = (Spinner) view.findViewById(R.id.spinner_select_decoder);
    decoderSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
            switch (position) {
              case 0:
                mGifDecoder = null;
                break;
              case 1:
                mGifDecoder = new GifDecoder();
                break;
              default:
                throw new IllegalArgumentException("Unknown decoder selected");
            }
            refreshAnimation();
          }

          @Override
          public void onNothingSelected(final AdapterView<?> parent) { }
        });
    decoderSpinner.setSelection(0);
  }

  private void refreshAnimation() {
    final Entry spinnerEntry = mSpinnerEntries[mSpinner.getSelectedItemPosition()];
    setAnimationUri(spinnerEntry.uri);
  }

  private void setAnimationUri(final Uri uri) {
    final PipelineDraweeControllerBuilder controllerBuilder =
        Fresco.newDraweeControllerBuilder()
            .setAutoPlayAnimations(true)
            .setOldController(mSimpleDraweeView.getController());
    final ImageDecodeOptionsBuilder optionsBuilder =
        ImageDecodeOptions.newBuilder().setMaxDimensionPx(4000);

    if (mGifDecoder != null) {
      optionsBuilder.setCustomImageDecoder(mGifDecoder);
    }

    controllerBuilder.setImageRequest(
        ImageRequestBuilder.newBuilderWithSource(uri)
            .setImageDecodeOptions(optionsBuilder.build())
            .build());
    mSimpleDraweeView.setController(controllerBuilder.build());
  }

  @Override
  public int getTitleId() {
    return R.string.format_gif_title;
  }

  private class SimpleUriListAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return mSpinnerEntries.length;
    }

    @Override
    public Entry getItem(final int position) {
      return mSpinnerEntries[position];
    }

    @Override
    public long getItemId(final int position) {
      return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
      final LayoutInflater layoutInflater = getLayoutInflater(null);

      final View view =
          convertView != null
              ? convertView
              : layoutInflater.inflate(
                  android.R.layout.simple_spinner_dropdown_item, parent, false);

      final TextView textView = (TextView) view.findViewById(android.R.id.text1);
      textView.setText(mSpinnerEntries[position].descriptionId);

      return view;
    }
  }

  private static class Entry {

    final int descriptionId;
    final Uri uri;

    private Entry(final int descriptionId, final Uri uri) {
      this.descriptionId = descriptionId;
      this.uri = uri;
    }
  }
}
