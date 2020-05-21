/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.samples.animation2.utils;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.drawable.AnimatedDrawable2;
import com.facebook.fresco.animation.drawable.AnimationListener;
import com.facebook.fresco.animation.drawable.BaseAnimationListener;
import javax.annotation.Nullable;

/** Helper class that manages animation controls: Play / pause, reset and a seekbar. */
public class AnimationControlsManager {

  private final AnimatedDrawable2 mAnimatedDrawable;
  @Nullable private final SeekBar mSeekBar;
  @Nullable private final ToggleButton mPlayPauseToggleButton;
  @Nullable private final View mResetButton;

  private AnimationListener mAnimationListener =
      new BaseAnimationListener() {
        @Override
        public void onAnimationStart(final AnimatedDrawable2 drawable) {
          if (mPlayPauseToggleButton != null) {
            mPlayPauseToggleButton.setChecked(true);
          }
        }

        @Override
        public void onAnimationStop(final AnimatedDrawable2 drawable) {
          if (mPlayPauseToggleButton != null) {
            mPlayPauseToggleButton.setChecked(false);
          }
        }

        @Override
        public void onAnimationFrame(final AnimatedDrawable2 drawable, final int frameNumber) {
          if (mSeekBar != null) {
            mSeekBar.setProgress(frameNumber);
          }
        }
      };

  public AnimationControlsManager(
      final AnimatedDrawable2 animatedDrawable,
      final @Nullable SeekBar seekBar,
      final @Nullable ToggleButton playPauseToggleButton,
      final @Nullable View resetButton) {
    mAnimatedDrawable = animatedDrawable;
    mSeekBar = seekBar;
    mPlayPauseToggleButton = playPauseToggleButton;
    mResetButton = resetButton;

    setupPlayPauseToggleButton();
    setupResetButton();
    setupSeekBar();

    mAnimatedDrawable.setAnimationListener(mAnimationListener);
    updateBackendData(mAnimatedDrawable.getAnimationBackend());
  }

  public void updateBackendData(final @Nullable AnimationBackend newBackend) {
    if (mSeekBar == null) {
      return;
    }
    if (newBackend != null) {
      mSeekBar.setMax(newBackend.getFrameCount() - 1);
    } else {
      mSeekBar.setMax(0);
    }
  }

  private void setupResetButton() {
    if (mResetButton == null) {
      return;
    }
    mResetButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(final View view) {
            mAnimatedDrawable.stop();
            mAnimatedDrawable.jumpToFrame(0);
          }
        });
  }

  private void setupPlayPauseToggleButton() {
    if (mPlayPauseToggleButton == null) {
      return;
    }
    mPlayPauseToggleButton.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            if (isChecked) {
              mAnimatedDrawable.start();
            } else {
              mAnimatedDrawable.stop();
            }
          }
        });
  }

  private void setupSeekBar() {
    if (mSeekBar == null) {
      return;
    }
    mSeekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
            if (fromUser) {
              mAnimatedDrawable.jumpToFrame(progress);
            }
          }

          @Override
          public void onStartTrackingTouch(final SeekBar seekBar) { }

          @Override
          public void onStopTrackingTouch(final SeekBar seekBar) { }
        });
  }
}
