package com.yinge.opengl.transform;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/13
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TransformRender implements GLSurfaceView.Renderer {

    public Cube mCube;
    private TransformMatrix mTransformMatrix;

    public TransformRender(Context context) {
        mCube = new Cube(context);
        mTransformMatrix = new TransformMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mCube.createProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        float rate= width / (float)height;

        mTransformMatrix.ortho(-rate * 6,rate * 6,-6,6,3,20);
        mTransformMatrix.setCamera(0, 0, 10, 0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        mCube.setMatrix(mTransformMatrix.getFinalMatrix());

        mCube.drawSelf();


        // y轴正方形平移
        mTransformMatrix.pushMatrix();
        mTransformMatrix.translate(0,3,0);
        mCube.setMatrix(mTransformMatrix.getFinalMatrix());
        mCube.drawSelf();
        mTransformMatrix.popMatrix();

        // y轴负方向平移，然后按xyz->(0,0,0)到(1,1,1)旋转30度
        mTransformMatrix.pushMatrix();
        mTransformMatrix.translate(0,-3,0);
        mTransformMatrix.rotate(30f,1,1,1);
        mCube.setMatrix(mTransformMatrix.getFinalMatrix());
        mCube.drawSelf();
        mTransformMatrix.popMatrix();


        // x轴负方向平移，然后按xyz->(0,0,0)到(1,-1,1)旋转120度，在放大到0.5倍
        mTransformMatrix.pushMatrix();
        mTransformMatrix.translate(-3,0,0);
        mTransformMatrix.scale(0.5f,0.5f,0.5f);

        // 在以上变换的基础上再进行变换
        mTransformMatrix.pushMatrix();
        mTransformMatrix.translate(12,0,0);
        mTransformMatrix.scale(1.0f,2.0f,1.0f);
        mTransformMatrix.rotate(30f,1,2,1);
        mCube.setMatrix(mTransformMatrix.getFinalMatrix());
        mCube.drawSelf();
        mTransformMatrix.popMatrix();

        // 接着被中断的地方执行
        mTransformMatrix.rotate(30f,-1,-1,1);
        mCube.setMatrix(mTransformMatrix.getFinalMatrix());
        mCube.drawSelf();
        mTransformMatrix.popMatrix();





    }
}
