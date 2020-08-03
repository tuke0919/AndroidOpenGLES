package com.yinge.opengl.image;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.yinge.opengl.image.filter.AbsImageFilter;

import java.io.IOException;

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
public class GLImageView extends GLSurfaceView {

    private GLImageRender mRender;

    public GLImageView(Context context) {
        super(context);
        init();
    }

    public GLImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        setEGLContextClientVersion(2);
        mRender = new GLImageRender(getContext());
        setRenderer(mRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);


        try {
            mRender.setBitmap(BitmapFactory.decodeStream(getResources().getAssets().open("texture/fengj.png")));
            requestRender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GLImageRender getRender() {
        return mRender;
    }

    /**
     * 设置过滤器
     * @param filter
     */
    public void setFilter(AbsImageFilter filter) {
        mRender.setImageFilter(filter);

    }


}
