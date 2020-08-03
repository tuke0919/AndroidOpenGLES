package com.yinge.opengl.transform;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.Stack;

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
public class TransformMatrix {

    // 相机矩阵
    private float[] mMatrixCamera=new float[16];
    // 投影矩阵
    private float[] mMatrixProjection=new float[16];
    // 总变换矩阵
    private float[] mMVPMatrix = new float[16];

    //原始矩阵
    private float[] mMatrixCurrent=
                   {1,0,0,0,
                    0,1,0,0,
                    0,0,1,0,
                    0,0,0,1};

    // 变换矩阵堆栈
    private Stack<float[]> mStack;


    public TransformMatrix(){
        mStack = new Stack<>();
    }

    /**
     * 保护现场
     */
    public void pushMatrix() {
        mStack.push(Arrays.copyOf(mMatrixCurrent, mMatrixCurrent.length));
    }

    /**
     * 恢复现场
     */
    public void popMatrix(){
        mMatrixCurrent = mStack.pop();
    }

    /**
     * 平移
     * @param x x轴平移的距离
     * @param y y轴平移的距离
     * @param z z轴平移的距离
     */
    public void translate(float x, float y, float z) {
        Matrix.translateM(mMatrixCurrent, 0, x, y, z);
    }

    /**
     * 旋转
     * @param x 旋转轴 在x方向的分量
     * @param y 旋转轴 在y方向的分量
     * @param z 旋转轴 在z方向的分量
     */
    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mMatrixCurrent, 0, angle, x, y, z);
    }

    /**
     * 缩放
     * @param x 在x方向的缩放倍数，或者因子
     * @param y 在y方向的缩放倍数，或者因子
     * @param z 在z方向的缩放倍数，或者因子
     */
    public void scale(float x,float y,float z) {
        Matrix.scaleM(mMatrixCurrent,0,x,y,z);
    }


    /**
     * 翻转
     * @param m
     * @param x x方向反转
     * @param y y方向反转
     * @return
     */
    public static float[] flip(float[] m,boolean x, boolean y){
        if(x||y){
            Matrix.scaleM(m,0,x ? -1 : 1, y ? -1 : 1,1);
        }
        return m;
    }

    /**
     * 设置摄像机矩阵
     * @param eyeX 摄像机位置x，y，z
     * @param eyeY
     * @param eyeZ
     * @param tx   目标点坐标 x, y, z
     * @param ty
     * @param tz
     * @param upx  up向量在x，y，z轴的分量
     * @param upy
     * @param upz
     */
    public void setCamera(float eyeX, float eyeY, float eyeZ,
                          float tx, float ty, float tz,
                          float upx,float upy, float upz) {

        Matrix.setLookAtM(mMatrixCamera,0, eyeX,eyeY, eyeZ, tx, ty, tz, upz, upy, upz);
    }

    /**
     * 设置 透视投影矩阵
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    public void frustum(float left,float right,float bottom,float top,float near,float far){
        Matrix.frustumM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    /**
     * 设置 正交投影矩阵
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     */
    public void ortho(float left, float right, float bottom, float top, float near, float far){
        Matrix.orthoM(mMatrixProjection,0, left, right, bottom, top, near, far);
    }

    /**
     * 获取 总变换矩阵
     * @return
     */
    public float[] getFinalMatrix(){
        float[] ans=new float[16];
        Matrix.multiplyMM(ans,0,mMatrixCamera,0,mMatrixCurrent,0);
        Matrix.multiplyMM(ans,0,mMatrixProjection,0,ans,0);
        return ans;
    }
}
