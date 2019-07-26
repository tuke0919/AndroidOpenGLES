package com.yinge.opengl.magic;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.yinge.opengl.R;
import com.yinge.opengl.camera.SavePictureTask;
import com.yinge.opengl.camera.camera.CameraGlSurfaceView;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.util.OpenGlCameraSdk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * 美颜，滤镜相机
 */
public class CameraFilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MODE_PIC = 1;
    private static final int MODE_VIDEO = 2;

    // 主视图
    private CameraGlSurfaceView mCameraGlSurfaceView;

    // 滤镜列表
    private LinearLayout mCameraFilterLayout;
    private RecyclerView mFilterRecyclerView;
    private ImageView mCloseFilter;

    // 适配器
    private CameraFilterAdapter mAdapter;

    // 美颜，快门，滤镜
    private ImageView mBtnShutter;
    private ImageView mBtnBeauty;
    private ImageView mBtnFilter;

    // 模式，切换摄像头
    private ImageView mBtnMode;
    private ImageView mBtnSwitch;

    // 当前模式
    private int mCurrentMode = MODE_PIC;

    // 动画
    private ObjectAnimator mShutterAnimator;

    // 是否在录视频
    private boolean isRecording = false;

    // 滤镜数组
    private final FilterType[] mFilterTypes = new FilterType[]{
            FilterType.NONE,
            FilterType.FAIRYTALE,
            FilterType.SUNRISE,
            FilterType.SUNSET,
            FilterType.WHITECAT,
            FilterType.BLACKCAT,
            FilterType.SKINWHITEN,
            FilterType.HEALTHY,
            FilterType.SWEETS,
            FilterType.ROMANCE,
            FilterType.SAKURA,
            FilterType.WARM,
            FilterType.ANTIQUE,
            FilterType.NOSTALGIA,
            FilterType.CALM,
            FilterType.LATTE,
            FilterType.TENDER,
            FilterType.COOL,
            FilterType.EMERALD,
            FilterType.EVERGREEN,
            FilterType.CRAYON,
            FilterType.SKETCH,
            FilterType.AMARO,
            FilterType.BRANNAN,
            FilterType.BROOKLYN,
            FilterType.EARLYBIRD,
            FilterType.FREUD,
            FilterType.HEFE,
            FilterType.HUDSON,
            FilterType.INKWELL,
            FilterType.KEVIN,
            FilterType.LOMO,
            FilterType.N1977,
            FilterType.NASHVILLE,
            FilterType.PIXAR,
            FilterType.RISE,
            FilterType.SIERRA,
            FilterType.SUTRO,
            FilterType.TOASTER2,
            FilterType.VALENCIA,
            FilterType.WALDEN,
            FilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_camera);
        initView();
    }

    private void initView(){
        mCameraGlSurfaceView = (CameraGlSurfaceView)findViewById(R.id.camera_gl_surfaceview);
        mCameraFilterLayout = (LinearLayout)findViewById(R.id.camera_filter_layout);
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.camera_filter_recyclerview);
        mCloseFilter = findViewById(R.id.btn_camera_close_filter);

        // 美颜，快门，滤镜
        mBtnShutter = (ImageView)findViewById(R.id.btn_camera_shutter);
        mBtnBeauty = (ImageView)findViewById(R.id.btn_camera_beauty);
        mBtnFilter = (ImageView)findViewById(R.id.btn_camera_filter);

        // 模式
        mBtnMode = (ImageView)findViewById(R.id.btn_camera_mode);
        // 切换摄像头
        mBtnSwitch = findViewById(R.id.btn_camera_switch);

        mBtnShutter.setOnClickListener(this);
        mBtnBeauty.setOnClickListener(this);
        mBtnFilter.setOnClickListener(this);
        mBtnMode.setOnClickListener(this);
        mBtnSwitch.setOnClickListener(this);
        mCloseFilter.setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterRecyclerView.setLayoutManager(manager);

        // 设置适配器
        mAdapter = new CameraFilterAdapter(this, Arrays.asList(mFilterTypes));
        mFilterRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(new CameraFilterAdapter.onFilterChangeListener() {
            @Override
            public void onFilterChanged(FilterType filterType) {
                // 设置滤镜
                mCameraGlSurfaceView.setFilter(filterType);
            }
        });

        // 初始化动画
        mShutterAnimator = ObjectAnimator.ofFloat(mBtnShutter,"rotation",0,360);
        mShutterAnimator.setDuration(500);
        mShutterAnimator.setRepeatCount(ValueAnimator.INFINITE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(mCurrentMode == MODE_PIC) {
                takePhoto();
            } else {
                takeVideo();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera_mode:
                switchMode();
                break;
            case R.id.btn_camera_switch:
                switchCamera();
                break;
            case R.id.btn_camera_shutter:
                cameraShutter(v);
                break;
            case R.id.btn_camera_filter:
                cameraFilter();
                break;
            case R.id.btn_camera_beauty:
                cameraBeauty();
                break;
            case R.id.btn_camera_close_filter:
                closeCameraFilter();
                break;
        }
    }

    /**
     * 切换 模式
     */
    private void switchMode(){
        if(mCurrentMode == MODE_PIC){
            mCurrentMode = MODE_VIDEO;
            mBtnMode.setImageResource(R.drawable.icon_camera_normal);
        }else{
            mCurrentMode = MODE_PIC;
            mBtnMode.setImageResource(R.drawable.icon_video);
        }
    }

    /**
     * 拍照
     */
    public void cameraShutter(View view) {

        if (PermissionChecker.checkSelfPermission(CameraFilterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraFilterActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, view.getId());
        } else {
            if(mCurrentMode == MODE_PIC) {
                takePhoto();
            } else {
                takeVideo();
            }
        }
    }

    /**
     * 拍照，保存图片
     */
    private void takePhoto(){
        mCameraGlSurfaceView.savePicture(new SavePictureTask(getOutputMediaFile(),null));
    }

    /**
     * 录像
     */
    private void takeVideo(){
        if(isRecording) {
            mShutterAnimator.end();
            mCameraGlSurfaceView.stopRecord();
        }else {
            mShutterAnimator.start();
            mCameraGlSurfaceView.startRecord();
        }
        isRecording = !isRecording;
    }

    /**
     * 显示滤镜列表
     */
    private void cameraFilter(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mCameraFilterLayout, "translationY", mCameraFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mBtnShutter.setClickable(false);
                mCameraFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mCameraGlSurfaceView.switchCamera();

    }

    /**
     * 显示美颜dialog
     */
    public void cameraBeauty() {
        new AlertDialog.Builder(CameraFilterActivity.this)
                .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, OpenGlCameraSdk.getInstance().getBeautyLevel(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 设置等级
                                OpenGlCameraSdk.getInstance().setBeautyLevel(which);
                                mCameraGlSurfaceView.onBeautyLevelChanged();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", null)
                .show();
    }


    /**
     * 关闭相机滤镜列表
     */
    private void closeCameraFilter(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mCameraFilterLayout, "translationY", 0 ,  mCameraFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCameraFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCameraFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }
        });
        animator.start();
    }


    /**
     * @return 输出文件路径
     */
    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MagicCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        String timeStamp = System.currentTimeMillis() + "";
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }



}
