/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.samples.scrollperf.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.facebook.samples.scrollperf.R;
import com.facebook.samples.scrollperf.util.SizeUtil;

/** A simple Preference containing a SeekBar in order to select a size */
public class SizePreferences extends Preference implements SeekBar.OnSeekBarChangeListener {

  // We always use half of the width as default
  private static final int DEFAULT_SIZE_VALUE = SizeUtil.DISPLAY_WIDTH / 2;

  private SeekBar mSeekBar;

  private TextView mSeekBarValueTextView;
  private TextView mTitleView;

  private int mProgressValue;

  private int mMaxValue;

  public SizePreferences(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    setLayoutResource(R.layout.size_preference);
  }

  public SizePreferences(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public SizePreferences(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public void setSeekBarMaxValue(final int maxValue) {
    mMaxValue = maxValue;
  }

  @Override
  public void onBindViewHolder(final PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);
    // We get the reference to the mSeekBar
    mSeekBar = (SeekBar) holder.findViewById(R.id.size_seek_bar);
    mSeekBar.setMax(mMaxValue);
    mSeekBar.setOnSeekBarChangeListener(this);
    mSeekBarValueTextView = (TextView) holder.findViewById(R.id.seek_bar_value);
    mTitleView = (TextView) holder.findViewById(R.id.title);
    mTitleView.setText(getTitle());
    // This is called after the initial value is set
    mSeekBar.setProgress(mProgressValue);
    updateCurrentValue(mProgressValue);
  }

  @Override
  public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
    updateCurrentValue(progress);
  }

  @Override
  public void onStartTrackingTouch(final SeekBar seekBar) { }

  @Override
  public void onStopTrackingTouch(final SeekBar seekBar) { }

  @Override
  protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue) {
    super.onSetInitialValue(restorePersistedValue, defaultValue);
    final int valueToDisplay;
    if (restorePersistedValue) {
      valueToDisplay = getPersistedInt(DEFAULT_SIZE_VALUE);
    } else {
      valueToDisplay = (Integer) defaultValue;
    }
    updateCurrentValue(valueToDisplay);
  }

  @Override
  protected Object onGetDefaultValue(final TypedArray a, final int index) {
    return a.getInt(index, 0);
  }

  public void updateCurrentValue(final int progress) {
    if (shouldPersist()) {
      persistInt(progress);
    }
    if (progress != mProgressValue) {
      mProgressValue = progress;
      notifyChanged();
    }
    if (mSeekBarValueTextView != null) {
      final String valueStr = getContext().getString(R.string.size_label_format, progress);
      mSeekBarValueTextView.setText(valueStr);
    }
  }
}
