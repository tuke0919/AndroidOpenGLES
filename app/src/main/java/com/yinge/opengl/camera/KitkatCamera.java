package com.yinge.opengl.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/13
 * @email tuke@xxx.com
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

    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open(cameraId);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            // 获取合适的尺寸
            mPreviewSize = getPropertySize(parameters.getSupportedPreviewSizes(), mSizeConfig.rate, mSizeConfig.minPreviewWidth);
            mPictureSize = getPropertySize(parameters.getSupportedPictureSizes(), mSizeConfig.rate, mSizeConfig.minPictureWidth);;

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
        if(mCamera!=null){
            try{
                mCamera.stopPreview();
                mCamera.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Point getPreviewSize() {
        return mOutPreviewSize;
    }

    @Override
    public Point getPictureSize() {
        return mOutPictureSize;
    }

    @Override
    public boolean switchCamera(int cameraId) {
        stopPreview();
        open(cameraId);
        return false;
    }

    @Override
    public void takePhoto(TakePhotoCallback callback) {

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
