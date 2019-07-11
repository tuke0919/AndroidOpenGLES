package com.yinge.opengl.image;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/11
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class GLImageView extends GLSurfaceView {

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

        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


}
