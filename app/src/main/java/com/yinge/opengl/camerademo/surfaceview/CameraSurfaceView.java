package com.yinge.opengl.camerademo.surfaceview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yinge.opengl.camerademo.camera.CameraProxy;

import static com.yinge.opengl.camerademo.camera.CameraProxy.TAG;


public class CameraSurfaceView extends SurfaceView {

    // 相机
    private CameraProxy mCameraProxy;

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private float mOldDistance;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(mSurfaceHolderCallback);
        // 实例化相机
        mCameraProxy = new CameraProxy((Activity) context);
    }

    private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e(TAG, "surfaceCreated： openCamera" );
            // 打开相机
            mCameraProxy.openCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            int previewWidth = mCameraProxy.getPreviewWidth();
            int previewHeight = mCameraProxy.getPreviewHeight();

            Log.e(TAG, "surfaceChanged： startPreview - "
                    + " previewWidth = " + previewWidth
                    + " previewHeight = " + previewHeight);

            Log.e(TAG, "surfaceChanged： startPreview - "
                    + " width = " + width
                    + " height = " + height);

            if (width > height) {
                setAspectRatio(previewWidth, previewHeight);
            } else {
                setAspectRatio(previewHeight, previewWidth);
            }
            mCameraProxy.startPreview(holder);


        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, "surfaceDestroyed： releaseCamera ");
            mCameraProxy.releaseCamera();
        }
    };

    /**
     * @param width
     * @param height
     */
    private void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;

        Log.d(TAG, "setAspectRatio："
                + " mRatioWidth = " + width
                + " mRatioHeight = " + height);

        requestLayout();
    }

    public CameraProxy getCameraProxy() {
        return mCameraProxy;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);

            Log.w(TAG, "onMeasure：ratio = 0"
                    + " width = " + width
                    + " height = " + height);

        } else {
            // 使SurfaceView和Image保持狂宽高比
            float ratio = mRatioWidth * 1f / mRatioHeight ;
            if (width < height * ratio ) {

                Log.w(TAG, "onMeasure：ratio = " + ratio
                        + " width = " + width
                        + " width * mRatioHeight / mRatioWidth = " + width * mRatioHeight / mRatioWidth);
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {

                Log.w(TAG, "onMeasure：ratio = " + ratio
                        + " width = " + (int) (height * ratio)
                        + " height = " + height);

                setMeasuredDimension((int) (height * ratio), height);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            // 点击聚焦
            mCameraProxy.focusOnPoint((int) event.getX(), (int) event.getY(), getWidth(), getHeight());
            return true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDistance = getFingerSpacing(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float newDistance = getFingerSpacing(event);
                if (newDistance > mOldDistance) {
                    mCameraProxy.handleZoom(true);
                } else if (newDistance < mOldDistance) {
                    mCameraProxy.handleZoom(false);
                }
                mOldDistance = newDistance;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
