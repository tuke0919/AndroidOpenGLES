package com.yinge.opengl.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.View;

import com.yinge.opengl.util.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：正三角形
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/7
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TriangleWithRegular extends Shape {

    // 每个坐标是三维
    private static final int COORDS_PER_VERTEX = 3;
    // 顶点坐标数组
    private static float triangleCoords[] = {
            0.5f,  0.5f, 0.0f,  // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f   // bottom right
    };

    // 顶点缓存数据
    private FloatBuffer vertexBuffer;
    // 主程序
    private int mProgram;
    // 顶点着色器 位置句柄
    private int mPositionHandle;
    // 片元着色器 颜色句柄
    private int mColorHandle;
    // 顶点的数目
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    // 顶点之间的偏移量
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    // 设置颜色，依次为红绿蓝和透明通道 此处是白色
    private float color[] = {1.0f, 1.0f, 1.0f, 1.0f};


    // 矩阵相关
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    // 矩阵的句柄
    private int mMatrixHandle;

    public TriangleWithRegular(View view) {
        super(view);

        // 申请底层空间，一个float占4个字节
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // 设置字节顺序
        byteBuffer.order(ByteOrder.nativeOrder());
        // 转换为float型缓冲池
        vertexBuffer = byteBuffer.asFloatBuffer();
        // 向缓冲区中放入顶点坐标数据
        vertexBuffer.put(triangleCoords);
        //设置缓冲区起始位置
        vertexBuffer.position(0);

        // 加载并编译 顶点着色器
        String vertexShaderCode1 = OpenGlUtils.loadShaderSrcFromAssetFile(mView.getResources(), "shape/vshader" + "/vRegularTriangle.glsl");
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode1);

        // 加载并编译 片元着色器
        String fragmentShaderCode1 = OpenGlUtils.loadShaderSrcFromAssetFile(mView.getResources(), "shape/fshader" + "/fRegularTriangle.glsl");
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode1);

        // 创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        // 将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        // 将片元着色器加入到程序
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 连接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        float ratio =(float) width / height;
        // 设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // 设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // 计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // 将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);

        //获取变换矩阵vMatrix成员句柄
        mMatrixHandle= GLES20.glGetUniformLocation(mProgram,"vMatrix");
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandle,1,false,mMVPMatrix,0);

        // 获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // 获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color,0);

        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // 禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}
