package com.yinge.opengl.camera.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.SparseArray;

import com.yinge.opengl.util.OpenGlUtils;
import com.yinge.opengl.util.ScaleTypeMatrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/14
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class AbsOesImageFilter implements GLSurfaceView.Renderer {

    // 顶点坐标
    private float mVertexPositions[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
             1.0f, 1.0f,
             1.0f, -1.0f,
    };

    // 纹理坐标
    private float[] mTextureCoords={
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };


    public static final float[] IDENTITY= ScaleTypeMatrix.getIdentityMatrix();

    protected Context mContext;

    protected int mProgram;

    // 顶点坐标 引用
    protected int glVertexPositionHandle;
    // 纹理坐标 引用
    protected int glTextureCoordsHandle;

    protected int glMVPMatrixHandle;

    protected int glTextureHandle;

    protected FloatBuffer mVertexPositionBuffer;
    protected FloatBuffer mTextureCoordsBuffer;

    protected ShortBuffer mIndexBuffer;

    // 总变换矩阵
    protected float[] mMVPMatrix= Arrays.copyOf(IDENTITY,16);

    // 纹理单元号
    private int mTextureType = 0;
    // 纹理id
    private int mTextureId = 0;

    private SparseArray<boolean[]> mBoolArrays;
    private SparseArray<int[]> mIntArrays;
    private SparseArray<float[]> mFloatArrays;


    public AbsOesImageFilter(Context context) {
        this.mContext = context;
        initDatas();
        initProgram();
    }

    /**
     * Buffer初始化
     */
    protected void initDatas(){
        ByteBuffer a = ByteBuffer.allocateDirect(mVertexPositions.length * 4);
        a.order(ByteOrder.nativeOrder());
        mVertexPositionBuffer = a.asFloatBuffer();
        mVertexPositionBuffer.put(mVertexPositions);
        mVertexPositionBuffer.position(0);

        ByteBuffer b = ByteBuffer.allocateDirect(mTextureCoords.length * 4);
        b.order(ByteOrder.nativeOrder());
        mTextureCoordsBuffer = b.asFloatBuffer();
        mTextureCoordsBuffer.put(mTextureCoords);
        mTextureCoordsBuffer.position(0);
    }

    /**
     * 创建主程序
     */
    protected void initProgram() {
        mProgram = OpenGlUtils.createProgram(mContext.getResources(), getVertexResPath(), getFragmentResPath());
    }

    /**
     * @return 顶点着色器路径
     */
    public abstract String getVertexResPath();

    /**
     * @return 片元着色器路径
     */
    public abstract String getFragmentResPath();


    /**
     * 初始化因引用
     */
    protected void initHandle() {
        glVertexPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        glTextureCoordsHandle = GLES20.glGetAttribLocation(mProgram,"vCoord");
        glMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        glTextureHandle = GLES20.glGetUniformLocation(mProgram,"vTexture");

        initOtherHandle();
    }

    /**
     * 初始化其他引用
     */
    public abstract void initOtherHandle();

    /**
     * @param width
     * @param height
     */
    public abstract void onSizeChanged(int width, int height);


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      initProgram();
      initHandle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
       onSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
       onDrawFrame();
    }

    /**
     * 绘图
     */
    protected void onDrawFrame() {
        onClear();
        GLES20.glUseProgram(mProgram);
        // 设置其他引用
        setOtherHandle();
        // 绑定纹理
        onBindTexture();

        // 设置矩阵参数
        GLES20.glUniformMatrix4fv(glMVPMatrixHandle,1,false, mMVPMatrix,0);

        GLES20.glEnableVertexAttribArray(glVertexPositionHandle);
        GLES20.glVertexAttribPointer(glVertexPositionHandle, 2 , GLES20.GL_FLOAT, false, 0,mVertexPositionBuffer);

        GLES20.glEnableVertexAttribArray(glTextureCoordsHandle);
        GLES20.glVertexAttribPointer(glTextureCoordsHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureCoordsBuffer);
        // 三角形绘图
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(glVertexPositionHandle);
        GLES20.glDisableVertexAttribArray(glTextureCoordsHandle);
    }

    /**
     * 设置其他引用
     */
    public abstract void setOtherHandle();


    /**
     * 绑定默认纹理
     */
    protected void onBindTexture(){
        // 激活纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureType);
        // 绑定纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId());
        // 设置纹理单元
        GLES20.glUniform1i(glTextureHandle, mTextureType);
    }

    public void setMatrix(float[] matrix){
        this.mMVPMatrix = matrix;
    }

    public float[] getMatrix(){
        return mMVPMatrix;
    }

    public final void setTextureType(int type){
        this.mTextureType = type;
    }

    public final int getTextureType(){
        return mTextureType;
    }

    public final int getTextureId(){
        return mTextureId;
    }

    public final void setTextureId(int textureId){
        this.mTextureId = textureId;
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 创建 位图纹理id
     * @param mBitmap
     * @return
     */
    public int createBitmapTexture(Bitmap mBitmap) {
        int[] texture = new int[1];
        if (mBitmap != null && !mBitmap.isRecycled()) {
            // 生成纹理，得到纹理id
            GLES20.glGenTextures(1, texture, 0);
            // 绑定纹理id
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // 设置纹理参数

            // 设置最小过滤器 为 最近采样： 使用纹理坐标最接近的颜色作为需要绘制的颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            // 设置最大功过滤器 为 线性采样器：使用纹理坐标 附近的若干个颜色，加权平均 得到需要绘制的颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // 设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            // 设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // 根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        }
        return texture[0];
    }
}
