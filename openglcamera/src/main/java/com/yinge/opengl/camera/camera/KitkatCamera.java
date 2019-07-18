package com.yinge.opengl.camera.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/13
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class KitkatCamera implements ICamera {


    // 真实的相机
    private Camera mCamera;

    // 预览尺寸
    private Camera.Size mPreviewSize;
    // 图片尺寸
    private Camera.Size mPictureSize;
    // 方向
    public int mOrientation;
    // 0 - 后置 1 - 前置
    public int mCameraId = 0;

    // 默认配置
    private SizeConfig mSizeConfig;
    public Point mOutPreviewSize;
    public Point mOutPictureSize;

    private CameraSizeComparator mSizeComparator;


    public KitkatCamera() {
        mSizeConfig = new SizeConfig();
        mSizeConfig.minPictureWidth = 720;
        mSizeConfig.minPreviewWidth = 720;
        mSizeConfig.rate=1.778f;

        mSizeComparator = new CameraSizeComparator();

    }

    public Camera getCamera() {
        return mCamera;
    }

    public int getCameraId() {
        return mCameraId;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public boolean isFrontCamera() {
        return getCameraId() == 1;
    }

    @Override
    public boolean open() {
        return open(mCameraId);
    }

    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open(cameraId);
        mCameraId = cameraId;

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();

            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mOrientation = 90;
            parameters.setRotation(90);

            // 获取合适的尺寸
            mPreviewSize = getPropertySize(parameters.getSupportedPreviewSizes(), mSizeConfig.rate, mSizeConfig.minPreviewWidth);
            mPictureSize = getPropertySize(parameters.getSupportedPictureSizes(), mSizeConfig.rate, mSizeConfig.minPictureWidth);;

            Log.e("yinge", "mPreviewSize = " + mPreviewSize.width + "*" + mPreviewSize.height
                    + "mPictureSize = " + mPictureSize.width + "*" + mPictureSize.height);

            // 设置参数
            parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);

            // 实际输出宽高
            Camera.Size pre = parameters.getPreviewSize();
            Camera.Size pic = parameters.getPictureSize();

            mOutPictureSize = new Point(pic.height,pic.width);
            mOutPreviewSize = new Point(pre.height,pre.width);

            return true;
        }
        return false;
    }

    @Override
    public void setSizeConfig(SizeConfig sizeConfig) {
        this.mSizeConfig = sizeConfig;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (callback != null) {
                    callback.onPreviewFrame(data, mOutPreviewSize.x, mOutPreviewSize.y);
                }
            }
        });
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        try {
            mCamera.setPreviewTexture(texture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPreview() {
        if(mCamera!=null){
            mCamera.startPreview();
        }
    }

    @Override
    public void stopPreview() {
        if(mCamera != null){
            try{
                mCamera.stopPreview();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void releasePreview() {
        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public Camera.Size getPreviewSize() {
        return mPreviewSize;
    }

    @Override
    public Camera.Size getPictureSize() {
        return mPictureSize;
    }

    @Override
    public boolean switchCamera() {
        releasePreview();
        open(mCameraId == 1 ? 0 : 1);
        return false;
    }

    @Override
    public void takePhoto(final TakePhotoCallback callback) {
        // 目前只要压缩后的数据
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (callback != null) {
                    callback.onTakePhoto(data, mPictureSize.width, mPictureSize.height);
                }
            }
        });
    }

    /**
     * 设置方向
     * @param rotation
     */
    public void setRotation(int rotation){
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotation);
        mCamera.setParameters(params);
    }


    /**
     * 获取 合适的尺寸
     * @param supportSizeList 支持的尺寸列表
     * @param th
     * @param minWidth
     * @return
     */
    private Camera.Size getPropertySize(List<Camera.Size> supportSizeList, float th, int minWidth){
        // 排序
        Collections.sort(supportSizeList, mSizeComparator);
        int i = 0;
        for(Camera.Size s:supportSizeList){
            // 比例相同 且 高大于宽
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        // 没找到 用第一个
        if(i == supportSizeList.size()){
            i = 0;
        }
        return supportSizeList.get(i);
    }

    /**
     * 宽高比是否相同
     * @param s
     * @param rate
     * @return
     */
    private boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03) {
            return true;
        } else{
            return false;
        }
    }

    /**
     * 相机Size比较器
     */
    private class CameraSizeComparator implements Comparator<Camera.Size> {

        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.height == rhs.height){
                return 0;
            } else if(lhs.height > rhs.height){
                return 1;
            } else{
                return -1;
            }
        }
    }
}
