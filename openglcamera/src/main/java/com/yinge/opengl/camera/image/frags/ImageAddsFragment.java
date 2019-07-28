package com.yinge.opengl.camera.image.frags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yinge.opengl.camera.R;


@SuppressLint("ValidFragment")
public class ImageAddsFragment extends BaseEditFragment{

	public ImageAddsFragment(Context context) {
		super(context);
	}

	@Override
	protected boolean isChanged() {
		return false;
	}

	@Override
	protected void onDialogButtonClick(DialogInterface dialog) {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
