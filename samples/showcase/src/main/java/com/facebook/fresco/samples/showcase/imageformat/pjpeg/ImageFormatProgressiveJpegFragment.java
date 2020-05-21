/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.imageformat.pjpeg;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.misc.ImageUriProvider;
import com.facebook.fresco.samples.showcase.misc.LogcatImagePerfDataListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Progressive JPEG example that logs which frames of a progressive JPEG are rendered */
public class ImageFormatProgressiveJpegFragment extends BaseShowcaseFragment {

  private Entry[] mSpinnerEntries;

  private final DateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
  private final ImagePerfDataListener mImagePerfDataListener = new LogcatImagePerfDataListener();

  private SimpleDraweeView mSimpleDraweeView;
  private boolean mProgressiveRenderingEnabled;
  private TextView mDebugOutput;
  private ScrollView mDebugOutputScrollView;

  @Nullable
  @Override
  public View onCreateView(
      final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_format_progressive_jpeg, container, false);
  }

  @Override
  public void onViewCreated(final View view, final @Nullable Bundle savedInstanceState) {

    mSpinnerEntries =
        new Entry[] {
          new Entry(
              R.string.format_pjpeg_label_small,
              sampleUris().createSampleUri(ImageUriProvider.ImageSize.L)),
          new Entry(
              R.string.format_pjpeg_label_medium,
              sampleUris().createSampleUri(ImageUriProvider.ImageSize.M)),
          new Entry(
              R.string.format_pjpeg_label_large,
              sampleUris().createSampleUri(ImageUriProvider.ImageSize.S)),
          new Entry(R.string.format_pjpeg_label_slow, sampleUris().createPJPEGSlow()),
        };

    ProgressBarDrawable progressBarDrawable = new ProgressBarDrawable();
    progressBarDrawable.setColor(getResources().getColor(R.color.progress_bar_color));
    progressBarDrawable.setBackgroundColor(
        getResources().getColor(R.color.progress_bar_background));

    mSimpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.drawee_view);
    mSimpleDraweeView.getHierarchy().setProgressBarImage(progressBarDrawable);

    mDebugOutput = (TextView) view.findViewById(R.id.debug_output);
    mDebugOutputScrollView = (ScrollView) view.findViewById(R.id.debug_output_scroll_view);

    final SwitchCompat switchProgressiveRenderingEnabled =
        (SwitchCompat) view.findViewById(R.id.switch_progressive_enabled);
    switchProgressiveRenderingEnabled.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            mProgressiveRenderingEnabled = isChecked;
          }
        });

    mProgressiveRenderingEnabled = switchProgressiveRenderingEnabled.isChecked();

    final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
    spinner.setAdapter(new SimpleUriListAdapter());
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
            final Entry spinnerEntry = mSpinnerEntries[spinner.getSelectedItemPosition()];
            setImageUri(spinnerEntry.uri);
          }

          @Override
          public void onNothingSelected(final AdapterView<?> parent) { }
        });
    spinner.setSelection(0);
  }

  private void setImageUri(final Uri uri) {
    mDebugOutput.setText("");
    ImageRequest request =
        ImageRequestBuilder.newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(mProgressiveRenderingEnabled)
            .build();
    DraweeController controller =
        Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setRetainImageOnFailure(true)
            .setPerfDataListener(mImagePerfDataListener)
            .setControllerListener(
                new BaseControllerListener<ImageInfo>() {
                  @Override
                  public void onFinalImageSet(
                      final String id,
                      final @javax.annotation.Nullable ImageInfo imageInfo,
                      final @javax.annotation.Nullable Animatable animatable) {
                    if (imageInfo != null) {
                      QualityInfo qualityInfo = imageInfo.getQualityInfo();
                      logScan(qualityInfo, true);
                    }
                  }

                  @Override
                  public void onIntermediateImageSet(
                      final String id, final @javax.annotation.Nullable ImageInfo imageInfo) {
                    if (imageInfo != null) {
                      QualityInfo qualityInfo = imageInfo.getQualityInfo();
                      logScan(qualityInfo, false);
                    }
                  }

                  @Override
                  public void onIntermediateImageFailed(final String id, final Throwable throwable) {
                    mDebugOutput.append(
                        String.format(
                            Locale.getDefault(),
                            "onIntermediateImageFailed, %s\n",
                            throwable.getMessage()));
                  }
                })
            .build();
    mSimpleDraweeView.setController(controller);
  }

  private void logScan(final QualityInfo qualityInfo, final boolean isFinalImage) {
    mDebugOutput.append(
        String.format(
            Locale.getDefault(),
            "%s: %s, goodEnough=%b, fullQuality=%b, quality=%d\n\n",
            mDateFormat.format(new Date(System.currentTimeMillis())),
            isFinalImage ? "final" : "intermediate",
            qualityInfo.isOfGoodEnoughQuality(),
            qualityInfo.isOfFullQuality(),
            qualityInfo.getQuality()));
    // Scroll to the bottom
    mDebugOutputScrollView.post(
        new Runnable() {
          @Override
          public void run() {
            mDebugOutputScrollView.scrollTo(0, mDebugOutputScrollView.getBottom());
          }
        });
  }

  @Override
  public int getTitleId() {
    return R.string.format_pjpeg_title;
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
