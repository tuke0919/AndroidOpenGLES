package com.yinge.opengl.camera.image.frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;


@SuppressLint("ValidFragment")
public class ImageFrameFragment extends BaseEditFragment{

	public ImageFrameFragment(Context context) {
		super(context);

	}

	@Override
	protected boolean isChanged() {
		return false;
	}

	@Override
	protected void onDialogButtonClick(DialogInterface dialog) {

	}
}
