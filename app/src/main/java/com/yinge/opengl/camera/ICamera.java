package com.yinge.opengl.camera;

import android.graphics.Point;
import android.graphics.SurfaceTexture;

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
public interface ICamera {


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
     * 结束预览
     */
    void stopPreview();

    /**
     * @return 预览宽高
     */
    Point getPreviewSize();

    /**
     * @return 图片宽高
     */
    Point getPictureSize();

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
