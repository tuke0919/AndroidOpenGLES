package com.yinge.opengl.camera.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/13
 */
public interface ICamera {

    /**
     * @return
     */
    boolean open();


    /**
     * 打开相机
     * @param cameraId
     * @return
     */
    boolean open(int cameraId);

    /**
     * 设置尺寸配置
     * @param sizeConfig
     */
    void setSizeConfig(SizeConfig sizeConfig);


    /**
     * 预览帧回调
     * @param callback
     */
    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    /**
     * 设置预览纹理
     * @param texture
     */
    void setPreviewTexture(SurfaceTexture texture);

    /**
     * 开始预览
     */
    void startPreview();

    /**
     * 停止预览
     */
    void stopPreview();

    /**
     * 停止预览 并释放资源
     */
    void releasePreview();

    /**
     * @return 预览宽高
     */
    Camera.Size getPreviewSize();

    /**
     * @return 图片宽高
     */
    Camera.Size getPictureSize();

    /**
     * 切换摄像头
     * @param camereId
     * @return
     */
    boolean switchCamera(int camereId);

    /**
     * 拍照回调
     * @param callback
     */
    void takePhoto(TakePhotoCallback callback);


    /**
     * 拍照回调
     */
    interface TakePhotoCallback{
        /**
         * @param bytes
         * @param width
         * @param height
         */
        void onTakePhoto(byte[] bytes, int width, int height);
    }

    /**
     * 预览帧回调
     */
    interface PreviewFrameCallback{
        /**
         * @param bytes
         * @param width
         * @param height
         */
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

    /**
     *
     */
    public class SizeConfig{
        // 宽高比
        float rate;
        // 最小预览宽
        int minPreviewWidth;
        // 最小预览高
        int minPictureWidth;
    }

}
