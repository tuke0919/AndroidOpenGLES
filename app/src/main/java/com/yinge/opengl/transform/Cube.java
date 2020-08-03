package com.yinge.opengl.transform;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.widget.CheckBox;

import com.yinge.opengl.render.Shape;
import com.yinge.opengl.util.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;

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
public class Cube {

    // 顶点坐标
    final float mCubePositions[] = {
            -1.0f,1.0f,1.0f,    //正面左上0
            -1.0f,-1.0f,1.0f,   //正面左下1
            1.0f,-1.0f,1.0f,    //正面右下2
            1.0f,1.0f,1.0f,     //正面右上3
            -1.0f,1.0f,-1.0f,    //反面左上4
            -1.0f,-1.0f,-1.0f,   //反面左下5
            1.0f,-1.0f,-1.0f,    //反面右下6
            1.0f,1.0f,-1.0f,     //反面右上7
    };
    // 画图的索引
    final short mDrawIndexs[]={
            6,7,4,6,4,5,    //后面
            6,3,7,6,2,3,    //右面
            6,5,1,6,1,2,    //下面
            0,3,2,0,2,1,    //正面
            0,1,5,0,5,4,    //左面
            0,7,3,0,4,7,    //上面
    };
    // 颜色
    float color[] = {
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };

    private Context mContext;

    // 缓冲数据
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;
    private FloatBuffer mColorBuffer;

    // 主程序
    private int mProgram;

    // 着色器的变量引用
    private int glPositionHandle;
    private int glMatrixHandle;
    private int glColorHandle;

    // 变换矩阵
    private float[] mMatrix;

    public Cube(Context context) {
        this.mContext = context;
        initDatas();
    }

    private void initDatas() {
        ByteBuffer a = ByteBuffer.allocateDirect(mCubePositions.length * 4);
        a.order(ByteOrder.nativeOrder());
        mVertexBuffer = a.asFloatBuffer();
        mVertexBuffer.put(mCubePositions);
        mVertexBuffer.position(0);

        ByteBuffer b = ByteBuffer.allocateDirect(color.length * 4);
        b.order(ByteOrder.nativeOrder());
        mColorBuffer = b.asFloatBuffer();
        mColorBuffer.put(color);
        mColorBuffer.position(0);

        ByteBuffer c = ByteBuffer.allocateDirect(mDrawIndexs.length * 2);
        c.order(ByteOrder.nativeOrder());
        mIndexBuffer = c.asShortBuffer();
        mIndexBuffer.put(mDrawIndexs);
        mIndexBuffer.position(0);
    }

    /**
     * 创建程序
     */
    public void createProgram() {
        mProgram = OpenGlUtils.createProgram(mContext.getResources(), "transform/vcube.glsl", "transform/fcube.glsl");
        glPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        glMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        glColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

    }


    /**
     * @param matrix
     */
    public void setMatrix(float[] matrix){
        this.mMatrix = matrix;
    }


    /**
     * 绘图
     */
    public void drawSelf(){
        GLES20.glUseProgram(mProgram);
        // 赋值
        if (mMatrix != null) {
            GLES20.glUniformMatrix4fv(glMatrixHandle, 1, false, mMatrix, 0);
        }

        // 启用句柄
        GLES20.glEnableVertexAttribArray(glPositionHandle);
        GLES20.glEnableVertexAttribArray(glColorHandle);
        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(glPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        // 设置绘制三角形的颜色
        GLES20.glVertexAttribPointer(glColorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mDrawIndexs.length, GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

        // 禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(glPositionHandle);
        GLES20.glDisableVertexAttribArray(glColorHandle);

    }



}
