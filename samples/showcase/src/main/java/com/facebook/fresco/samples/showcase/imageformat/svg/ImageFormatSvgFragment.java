/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.imageformat.svg;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.CustomImageFormatConfigurator;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.misc.CheckerBoardDrawable;

/** SVG example. It has a toggle to enable / disable SVG support and displays 1 image. */
public class ImageFormatSvgFragment extends BaseShowcaseFragment {

  private SimpleDraweeView mSimpleDraweeView;
  private ShowRestartMessageDialog mShowRestartMessageDialog;

  @Nullable
  @Override
  public View onCreateView(
      final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_format_svg, container, false);
  }

  @Override
  public void onViewCreated(final View view, final @Nullable Bundle savedInstanceState) {
    mSimpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.drawee_view);
    mSimpleDraweeView.setImageURI(sampleUris().createSvgUri());

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

    SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.decoder_switch);
    switchCompat.setChecked(CustomImageFormatConfigurator.isSvgEnabled(getContext()));
    switchCompat.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            CustomImageFormatConfigurator.setSvgEnabled(getContext(), isChecked);
            getShowRestartMessageDialog().show(getChildFragmentManager(), null);
          }
        });
  }

  @Override
  public int getTitleId() {
    return R.string.format_svg_title;
  }

  private ShowRestartMessageDialog getShowRestartMessageDialog() {
    if (mShowRestartMessageDialog == null) {
      mShowRestartMessageDialog = new ShowRestartMessageDialog();
    }
    return mShowRestartMessageDialog;
  }

  public static class ShowRestartMessageDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
      // Use the Builder class for convenient dialog construction
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder
          .setMessage(R.string.message_application_needs_restart)
          .setPositiveButton(android.R.string.ok, null)
          .setNeutralButton(
              R.string.message_restart_now,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                  System.exit(0);
                }
              });
      return builder.create();
    }
  }
}
