package com.yinge.opengl.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

import com.yinge.opengl.image.filter.AbsImageFilter;
import com.yinge.opengl.image.filter.ContrastColorFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/11
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class GLImageRender implements GLSurfaceView.Renderer {

    private Context context;

    // 过滤器
    private AbsImageFilter mImageFilter;
    // 纹理图
    private Bitmap mBitmap;

    private int mWidth,mHeight;
    private EGLConfig mEGLConfig;
    private boolean mRefresh;


    public GLImageRender(Context context) {
        this.context = context;
        mImageFilter = new ContrastColorFilter(context, ContrastColorFilter.Filter.NONE);
    }

    /**
     * 设置过滤器
     * @param mImageFilter
     */
    public void setImageFilter(AbsImageFilter mImageFilter) {
        mRefresh = true;
        this.mImageFilter = mImageFilter;

        if (mBitmap != null) {
            mImageFilter.setBitmap(mBitmap);
        }
    }

    /**
     * 设置纹理图
     * @param mBitmap
     */
    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;

        if (mImageFilter != null) {
            mImageFilter.setBitmap(mBitmap);
        }
    }

    /**
     * @return
     */
    public AbsImageFilter getImageFilter() {
        return mImageFilter;
    }

    /**
     * @return
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }



    public void setRefresh() {
        mRefresh = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mEGLConfig = config;
        mImageFilter.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        mImageFilter.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(mRefresh && mWidth!=0 && mHeight!=0){
            mImageFilter.onSurfaceCreated(gl, mEGLConfig);
            mImageFilter.onSurfaceChanged(gl,mWidth,mHeight);
            mRefresh=false;
        }
        mImageFilter.onDrawFrame(gl);
    }
}
