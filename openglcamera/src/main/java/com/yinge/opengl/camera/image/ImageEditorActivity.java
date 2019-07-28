package com.yinge.opengl.camera.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.yinge.opengl.camera.R;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.image.frags.BaseEditFragment;
import com.yinge.opengl.camera.image.frags.ImageAddsFragment;
import com.yinge.opengl.camera.image.frags.ImageAdjustFragment;
import com.yinge.opengl.camera.image.frags.ImageBeautyFragment;
import com.yinge.opengl.camera.image.frags.ImageFilterFragment;
import com.yinge.opengl.camera.image.frags.ImageFrameFragment;

import java.io.InputStream;
import java.net.URL;

public class ImageEditorActivity extends AppCompatActivity {

    private final static int REQUEST_PICK_IMAGE = 1000;

    // 编辑按钮
    private RadioGroup mRadioGroup;
    // 管理器
    private FragmentManager mFragManager;
    // 对应的frag
    private Fragment[] mEditFragments;
    // tag
    private int mFragmentTag = -1;

    // SurfaceView
    private ImageGlSurfaceView mImageGlSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

        initViews();
        initFragments();
        initRadioButtons();
        initDatas();
    }

    public void initViews() {
        mImageGlSurfaceView = findViewById(R.id.glsurfaceview_image);
    }

    /**
     * 初始化 fragments
     */
    public void initFragments() {
        mEditFragments = new Fragment[5];

        // 编辑
        ImageAdjustFragment adjustFragment = new ImageAdjustFragment(this);
        adjustFragment.setOnHideListener(mOnHideListener);
        adjustFragment.setAdjustListener(new ImageAdjustFragment.OnAdjustListener() {
            @Override
            public void onAdjustFilter(int progress, FilterType currentFilterType) {

            }
        });
        adjustFragment.setOnFilterChangeListener(new BaseEditFragment.onFilterChangeListener() {
            @Override
            public void onSetFilter(FilterType filterType) {
                mImageGlSurfaceView.setFilter(filterType);
            }
        });
        mEditFragments[0] = adjustFragment;

        // 美颜
        ImageBeautyFragment beautyFragment = new ImageBeautyFragment(this);
        beautyFragment.setOnHideListener(mOnHideListener);
        mEditFragments[1] = beautyFragment;

        // 装饰
        ImageAddsFragment addsFragment = new ImageAddsFragment(this);
        addsFragment.setOnHideListener(mOnHideListener);
        mEditFragments[2] = addsFragment;

        // 滤镜
        ImageFilterFragment filterFragment = new ImageFilterFragment(this);
        filterFragment.setOnHideListener(mOnHideListener);
        filterFragment.setOnFilterChangeListener(new BaseEditFragment.onFilterChangeListener() {
            @Override
            public void onSetFilter(FilterType filterType) {
                mImageGlSurfaceView.setFilter(filterType);
            }
        });
        mEditFragments[3] = filterFragment;

        // 边框
        ImageFrameFragment frameFragment = new ImageFrameFragment(this);
        frameFragment.setOnHideListener(mOnHideListener);
        mEditFragments[4] = frameFragment;
    }

    /**
     * 初始化 按钮
     */
    public void initRadioButtons(){
        mRadioGroup = (RadioGroup)findViewById(R.id.image_edit_radiogroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.image_edit_adjust) {
                    if (!mEditFragments[0].isAdded()) {
                        mFragManager.beginTransaction()
                                .add(R.id.image_edit_fragment_container, mEditFragments[0])
                                .show(mEditFragments[0])
                                .commit();
                    } else {
                        mFragManager.beginTransaction().show(mEditFragments[0]).commit();
                    }
                    mFragmentTag = 0;

                } else if (checkedId == R.id.image_edit_beauty) {
                    if (!mEditFragments[1].isAdded()) {
                        mFragManager.beginTransaction()
                                .add(R.id.image_edit_fragment_container, mEditFragments[1])
                                .show(mEditFragments[1])
                                .commit();
                    } else {
                        mFragManager.beginTransaction().show(mEditFragments[1]).commit();
                    }
                    mFragmentTag = 1;

                } else if (checkedId == R.id.image_edit_adds) {
                    if (true) {
                        return;
                    }

                    if (!mEditFragments[2].isAdded()) {
                        mFragManager.beginTransaction()
                                .add(R.id.image_edit_fragment_container, mEditFragments[2])
                                .show(mEditFragments[2])
                                .commit();
                    } else {
                        mFragManager.beginTransaction().show(mEditFragments[2]).commit();
                    }
                    mFragmentTag = 2;

                } else if (checkedId == R.id.image_edit_filter) {
                    if (!mEditFragments[3].isAdded()) {
                        mFragManager.beginTransaction()
                                .add(R.id.image_edit_fragment_container, mEditFragments[3])
                                .show(mEditFragments[3])
                                .commit();
                    } else {
                        mFragManager.beginTransaction().show(mEditFragments[3]).commit();
                    }
                    mFragmentTag = 3;

                } else if (checkedId == R.id.image_edit_frame) {
                    if (true) {
                        return;
                    }
                    if (!mEditFragments[4].isAdded()) {
                        mFragManager.beginTransaction()
                                .add(R.id.image_edit_fragment_container, mEditFragments[4])
                                .show(mEditFragments[4])
                                .commit();
                    } else {
                        mFragManager.beginTransaction().show(mEditFragments[4]).commit();
                    }
                    mFragmentTag = 4;

                } else {
                    if (mFragmentTag != -1)
                        mFragManager.beginTransaction()
                                .hide(mEditFragments[mFragmentTag])
                                .commit();
                    mFragmentTag = -1;
                }
            }
        });
    }

    /**
     * 去相册选择图片
     */
    public void initDatas() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);
    }

    /**
     * 隐藏frag
     */
    private void hideFragment(){
        ((BaseEditFragment) mEditFragments[mFragmentTag]).onHide();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri mUri = data.getData();
                        InputStream inputStream;
                        if (mUri.getScheme().startsWith("http") || mUri.getScheme().startsWith("https")) {
                            inputStream = new URL(mUri.toString()).openStream();
                        } else {
                            inputStream = getContentResolver().openInputStream(mUri);
                        }
                        // 设置选择图片的bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        mImageGlSurfaceView.setImageBitmap(bitmap);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    finish();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private BaseEditFragment.onHideListener mOnHideListener = new BaseEditFragment.onHideListener() {

        @Override
        public void onHide() {
            mRadioGroup.check(View.NO_ID);
        }
    };
    
    @Override
    protected void onPause() {
        super.onPause();
        mImageGlSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageGlSurfaceView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(mFragmentTag != -1){
                    hideFragment();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
