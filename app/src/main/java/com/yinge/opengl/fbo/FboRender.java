package com.yinge.opengl.fbo;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.View;

import com.yinge.opengl.camera.filter.AbsOesImageFilter;
import com.yinge.opengl.camera.filter.GrayFilter;
import com.yinge.opengl.transform.TransformMatrix;
import com.yinge.opengl.util.ScaleTypeMatrix;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能： 概念地址：
 * https://www.2cto.com/kf/201611/567103.html
 * https://www.jianshu.com/p/78a64b8fb315
 *
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/14
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class FboRender implements GLSurfaceView.Renderer {

    private Context context;
    // glsl着色器
    private AbsOesImageFilter mImageFilter;
    // 位图
    private Bitmap mBitmap;
    // 字节缓存区
    private ByteBuffer mByteBuffer;

    // 帧缓冲对象 - 颜色、深度、模板附着点，纹理对象可以连接到帧缓冲区对象的颜色附着点
    private int[] mFrameBufferObject = new int[1];
    // 渲染缓冲对象 - 具有深度附着点 和 模板附着点
    private int[] mRenderBufferObject = new int[1];
    // 纹理id， id会对应一个纹理对象
    private int[] mTextureId = new int[2];

    private  Callback mCallback;


    public FboRender(Context context) {
        this.context = context;
        // 灰色过滤器
        mImageFilter = new GrayFilter(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mImageFilter.onSurfaceCreated(gl, config);
        // 设置总变换矩阵， x方向反转，y方向不反转
        mImageFilter.setMatrix(TransformMatrix.flip(ScaleTypeMatrix.getIdentityMatrix(),false,true));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mImageFilter.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
         if (mBitmap != null && !mBitmap.isRecycled()) {
             // 创建帧缓冲对象引用
             GLES20.glGenFramebuffers(1, mFrameBufferObject, 0);
             // 绑定帧缓冲，可以理解为 分配内存
             GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferObject[0]);

             // 创建 并绑定 渲染缓冲区对象
             createRenderBuffer();
             // 创建 并绑定 纹理对象
             createTexture2D();

             if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                 Log.i("", "Framebuffer error");
             }

             GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0); // 切换到系统窗口 缓冲区

             // 设置视图是 图片宽高 这样视图宽高比 就等于图片宽高比，就不用设置 投影矩阵和相机矩阵
             GLES20.glViewport(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
             // 把纹理id 1 设置进渲染管线
             mImageFilter.setTextureId(mTextureId[0]);
             // 开始绘图
             mImageFilter.onDrawFrame(gl);

             // 从帧缓冲区 读处理后的图像像素
             GLES20.glReadPixels(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mByteBuffer);

             // 回调
             if (mCallback != null) {
                 mCallback.onCall(mByteBuffer);
             }

             delete();
             mBitmap.recycle();
         }
    }


    /**
     * 创建渲染缓冲区对象
     */
    private void createRenderBuffer() {
        // 创建渲染缓冲区 引用
        GLES20.glGenRenderbuffers(mRenderBufferObject.length, mRenderBufferObject, 0);
        // 绑定渲染缓冲区，可以理解为 分配内存
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRenderBufferObject[0]);
        // 存储位图的 深度组件
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mBitmap.getWidth(), mBitmap.getHeight());
        // 将渲染缓冲区，附着在 帧缓存的 深度附着点上
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER,mFrameBufferObject[0]);

//        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0); 切换到系统窗口 缓冲区
    }

    /**
     * 创建 纹理对象
     */
    private void createTexture2D() {
        GLES20.glGenTextures(2, mTextureId, 0);
        for (int i = 0; i < 2; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[i]);
            if (i == 0) {
                // 第一个纹理对象 是给 渲染管线的
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0);
            } else {
                // 第二个纹理对象 是给 帧缓冲区的
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap.getWidth(), mBitmap.getHeight(),
                        0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            }
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        // 分配字节缓区大小， 一个像素4个字节
        mByteBuffer = ByteBuffer.allocate(mBitmap.getWidth() * mBitmap.getHeight() * 4);

        // 将纹理对象1，附着在 帧缓存的 颜色附着点上
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTextureId[1], 0);
    }


    /**
     * 清空缓冲区
     */
    private void delete() {
        GLES20.glDeleteTextures(2, mTextureId, 0);
        GLES20.glDeleteRenderbuffers(1, mFrameBufferObject, 0);
        GLES20.glDeleteFramebuffers(1, mRenderBufferObject, 0);
    }


    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    interface Callback{
        /**
         * @param data
         */
        void onCall(ByteBuffer data);
    }
}
