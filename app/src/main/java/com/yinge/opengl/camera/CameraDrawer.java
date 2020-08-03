package com.yinge.opengl.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.yinge.opengl.camera.filter.AbsOesImageFilter;
import com.yinge.opengl.camera.filter.OesFilter;
import com.yinge.opengl.transform.TransformMatrix;
import com.yinge.opengl.util.ScaleTypeMatrix;

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
public class CameraDrawer implements GLSurfaceView.Renderer {

    private float[] mMatrix = new float[16];

    // 接收相机硬件的图像流
    private SurfaceTexture mSurfaceTexture;
    // 过滤器
    private AbsOesImageFilter mOesFilter;
    // 相机id
    private int mCameraId = 1;

    // 视口宽
    private int mViewWidth;
    // 视口高
    private int mViewHeight;
    // 数据宽
    private int mDataWidth;
    // 数据高
    private int mDataHeight;


    public CameraDrawer(Context context) {
        mOesFilter = new OesFilter(context);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 创建外部纹理id，绑定图像流 到此纹理id
        int externalTextureId = createExternalTextureID();
        mSurfaceTexture = new SurfaceTexture(externalTextureId);

        // 创建程序，获取引用
        mOesFilter.onSurfaceCreated(gl, config);
        // 设置纹理id
        mOesFilter.setTextureId(externalTextureId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置 视图宽高
       setViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 把图像流的最新图像 更新到纹理目标上，即上面的外部纹理id
        if(mSurfaceTexture!=null){
            mSurfaceTexture.updateTexImage();
        }
        // 开始画图
        mOesFilter.onDrawFrame(gl);
    }

    /**
     * @return
     */
    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }

    /**
     * @param id
     */
    public void setCameraId(int id){
        this.mCameraId = id;
        calculateMVPMatrix();
    }

    /**
     * 设置数据尺寸
     * @param dataWidth
     * @param dataHeight
     */
    public void setDataSize(int dataWidth,int dataHeight){
        this.mDataWidth = dataWidth;
        this.mDataHeight = dataHeight;
        calculateMVPMatrix();
    }

    /**
     * 设置视口尺寸
     * @param width
     * @param height
     */
    public void setViewSize(int width,int height){
        this.mViewWidth = width;
        this.mViewHeight = height;
        calculateMVPMatrix();
    }

    /**
     * 计算变换矩阵
     */
    private void calculateMVPMatrix(){
        ScaleTypeMatrix.getMVPMatrix(mMatrix, this.mDataWidth, this.mDataHeight, this.mViewWidth, this.mViewHeight );

        if(mCameraId == 1){
            TransformMatrix.flip(mMatrix,true,false);
            Matrix.rotateM(mMatrix,0, 90,0, 0, 1);
        }else{
            Matrix.rotateM(mMatrix,0,270,0,0,1);
        }
        mOesFilter.setMatrix(mMatrix);
    }


    /**
     * @return 创建外部纹理id
     */
    private int createExternalTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        // 使用GL_TEXTURE_EXTERNAL_OES 创建纹理id
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

}
