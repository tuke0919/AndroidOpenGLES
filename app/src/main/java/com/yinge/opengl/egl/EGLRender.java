package com.yinge.opengl.egl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.yinge.opengl.camera.filter.AbsOesImageFilter;

import java.nio.IntBuffer;

/**
 * 功能：EGL环境 + 着色器 后台渲染图片
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/16
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class EGLRender {

    final static String TAG = "EGLRender";

    private int mWidth;
    private int mHeight;
    private EGLHelper mEGLHelper;

    // 着色器 封装
    private AbsOesImageFilter mImageFilter;
    // 待渲染图片
    private Bitmap mBitmap;
    private String mThreadOwner;

    public EGLRender(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;

        mEGLHelper = new EGLHelper(width, height);
    }

    public void setFilter(AbsOesImageFilter filter) {
        mImageFilter = filter;

        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "setFilter: This thread does not own the OpenGL context.");
            return;
        }
        // 走着色器流程 创建program，获取引用等, 参数无所谓
        mImageFilter.onSurfaceCreated(mEGLHelper.mGL10,mEGLHelper.mEglConfig);
        mImageFilter.onSurfaceChanged(mEGLHelper.mGL10, mWidth, mHeight);

    }

    /**
     * 设置 EGL环境的线程 用于检查 着色器渲染环境 应该是 EGL环境的线程
     * @param threadOwner
     */
    public void setThreadOwner(String threadOwner) {
        this.mThreadOwner = threadOwner;
    }

    /**
     * 设置 待渲染的图像
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    /**
     * @return 获取 渲染后的图片
     */
    public Bitmap getBitmap() {
        if (mImageFilter == null) {
            Log.e(TAG, "getBitmap: Renderer was not set.");
            return null;
        }

        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "getBitmap: This thread does not own the OpenGL context.");
            return null;
        }
        // 设置纹理id
        int bitmapTextureId = mImageFilter.createBitmapTexture(mBitmap);
        mImageFilter.setTextureId(bitmapTextureId);
        // 开始在Surface上绘图 参数无所谓
        mImageFilter.onDrawFrame(mEGLHelper.mGL10);

        // 返回渲染后的 图像
        return convertToBitmap();
    }


    /**
     * @return 像素 转换 为图像
     */
    private Bitmap convertToBitmap() {

        int[] intArray = new int[mWidth * mHeight];
        // 读取渲染后的 像素
        IntBuffer intBuffer = IntBuffer.allocate(mWidth * mHeight);
        mEGLHelper.mGL10.glReadPixels(0, 0 , mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);

        int[] intPixelsAfterRender = intBuffer.array();
        int[] target = new int[mWidth * mHeight];

        // 将倒置镜像反转图像 转换 为正面朝上的正常图像。
        for (int i = 0; i < mHeight; i++) {
            System.arraycopy(intPixelsAfterRender, i * mWidth, target, (mHeight - i - 1) * mWidth, mWidth);
        }

        // 从buffer 读像素
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(target));

        return bitmap;
    }

    /**
     *
     */
    public void destroy() {
        mEGLHelper.destroyEglContext();
    }

}
