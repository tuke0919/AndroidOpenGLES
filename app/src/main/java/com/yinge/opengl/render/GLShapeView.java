package com.yinge.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/7
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class GLShapeView extends GLSurfaceView {

    private GLShapeRender shapeRender;

    public GLShapeView(Context context) {
        super(context);
        init();
    }

    public GLShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(shapeRender = new GLShapeRender(this));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /**
     * 设置形状
     * @param clazz
     */
    public void setShape(Class<? extends Shape> clazz) {

        try {
            shapeRender.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
