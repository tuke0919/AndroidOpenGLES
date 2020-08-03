package com.yinge.opengl.image.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.yinge.opengl.util.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：使用本地图片 作为纹理的 纹理图片gles
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/11
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public abstract class AbsImageFilter implements GLSurfaceView.Renderer {


    /**
     * 图元的左上右下坐标
     */
    private final float[] vertexPositions = {
            -1.0f,1.0f,
            -1.0f,-1.0f,
            1.0f,1.0f,
            1.0f,-1.0f
    };

    /**
     * 图元四个定点对应的 纹理坐标
     */
    private final float[] textureCoords = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };


    private Context mContext;
    private int mProgram;

    private int glPositionHandle;
    private int glTextureHandle;
    private int glCoordinateHandle;

    private int glMatrixHandle;
    private int glIsHalfhandle;
    private int glUxyHandle;

    private Bitmap mBitmap;

    // 顶点坐标缓冲数据
    private FloatBuffer bPositionsBuffer;
    // 顶点的纹理坐标缓冲数据
    private FloatBuffer bCoordsBuffer;

    private String mVertexResPath;
    private String mFragmentResPath;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private int mTextureId;


    private boolean mIsHalf;
    private float mUxy;

    public AbsImageFilter(Context mContext, String vertexResPath, String fragmentResPath) {
        this.mContext = mContext;
        this.mVertexResPath = vertexResPath;
        this.mFragmentResPath = fragmentResPath;

        // 顶点坐标缓冲数据
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertexPositions.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        bPositionsBuffer = buffer.asFloatBuffer();
        bPositionsBuffer.put(vertexPositions);
        bPositionsBuffer.position(0);

        // 顶点对应的纹理坐标缓冲数据
        ByteBuffer coordsBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4);
        coordsBuffer.order(ByteOrder.nativeOrder());
        bCoordsBuffer = coordsBuffer.asFloatBuffer();
        bCoordsBuffer.put(textureCoords);
        bCoordsBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
        // 启用纹理
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        // 创建主程序
        mProgram = OpenGlUtils.createProgram(mContext.getResources(), mVertexResPath, mFragmentResPath);

        // 获取主程序 里的着色器变量引用
        glPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        glCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        glTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture");
        glMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        glIsHalfhandle = GLES20.glGetUniformLocation(mProgram,"vIsHalf" );
        glUxyHandle = GLES20.glGetUniformLocation(mProgram, "uXY");

        // 子类实现
        onGetOtherHandle(mProgram);

    }

    /**
     * 获取 着色器中其他引用
     * @param mProgram
     */
    public abstract void onGetOtherHandle(int mProgram);

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);

        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();

        float bmpRatio= bmpWidth / (float)bmpHeight;
        float viewRatio = width / (float)height;

        mUxy = viewRatio;

        if (width < height) {
            // 竖屏
            if(bmpRatio > viewRatio){
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, - bmpRatio / viewRatio , bmpRatio / viewRatio,3, 5);
            }else{
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, - bmpRatio / viewRatio, bmpRatio / viewRatio,3, 5);
            }

        } else {
            // 横屏

            if (bmpRatio > viewRatio) {
                Matrix.orthoM(mProjectionMatrix, 0, - viewRatio  * bmpRatio,viewRatio * bmpRatio, -1,1, 3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, - viewRatio / bmpRatio,viewRatio / bmpRatio, -1,1, 3, 5);
            }
        }
        // 设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0,0, 5f,0,0,0, 0, 1, 0);
        // 计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix,0,mViewMatrix,0);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        // 使用主程序
        GLES20.glUseProgram(mProgram);
        // 子类实现
        onSetOtherHandle();

        // 给引用赋值
        GLES20.glUniform1i(glIsHalfhandle, mIsHalf ? 1 : 0);
        GLES20.glUniform1f(glUxyHandle, mUxy);

        GLES20.glUniformMatrix4fv(glMatrixHandle, 1, false, mMVPMatrix, 0);

        // 启用顶点attribute数组引用
        GLES20.glEnableVertexAttribArray(glPositionHandle);
        GLES20.glEnableVertexAttribArray(glCoordinateHandle);

        GLES20.glUniform1i(glTextureHandle, 0);

        mTextureId = createTexture();

        // 给数组指针 赋值
        GLES20.glVertexAttribPointer(glCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, bCoordsBuffer);
        GLES20.glVertexAttribPointer(glPositionHandle, 2, GLES20.GL_FLOAT, false, 0, bPositionsBuffer);
        // 折线形式绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

    /**
     * 设置 其他类型的 引用值
     */
    public abstract void onSetOtherHandle();

    public int createTexture() {
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

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public boolean isIsHalf() {
        return mIsHalf;
    }

    public void setIsHalf(boolean mIsHalf) {
        this.mIsHalf = mIsHalf;
    }

    public float getUxy() {
        return mUxy;
    }

    public void setUxy(float mUxy) {
        this.mUxy = mUxy;
    }
}
