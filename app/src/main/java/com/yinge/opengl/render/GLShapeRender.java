package com.yinge.opengl.render;

import android.opengl.GLES20;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
public class GLShapeRender extends Shape {

    protected static final String TAG = GLShapeRender.class.getSimpleName();

    private Shape shape;
    private Class<? extends Shape> clazz = Triangle.class;

    public GLShapeRender(View view) {
        super(view);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);
        Log.e(TAG,"onSurfaceCreated");

        try {
            Constructor constructor = clazz.getDeclaredConstructor(View.class);
            constructor.setAccessible(true);
            shape= (Shape) constructor.newInstance(mView);


        } catch (Exception e) {
            shape=new Triangle(mView);
        }
        shape.onSurfaceCreated(gl,config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG,"onSurfaceChanged");
        GLES20.glViewport(0,0, width, height);

        shape.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.e(TAG,"onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

        shape.onDrawFrame(gl);
    }

    public void setShape(Class<? extends Shape> clazz) {
        this.clazz = clazz;
    }
}
