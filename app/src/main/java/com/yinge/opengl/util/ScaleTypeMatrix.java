package com.yinge.opengl.util;

import android.opengl.Matrix;

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
public class ScaleTypeMatrix {


    /**
     * @return 单位矩阵
     */
    public static float[] getIdentityMatrix(){
        return new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
    }

    /**
     * 计算 mvp矩阵
     * @param mvpMatrix   最终mvp矩阵
     * @param imageWidth  图像宽
     * @param imageHeight 图像高
     * @param viewWidth   视图宽
     * @param viewHeight  视图高
     */
    public static void getMVPMatrix(float[] mvpMatrix,int imageWidth,int imageHeight,int viewWidth,int viewHeight){

        if(imageHeight > 0&& imageWidth >0 && viewWidth > 0 && viewHeight > 0){

            float viewRatio = (float)viewWidth / viewHeight;
            float imageRatio = (float)imageWidth / imageHeight;
            float[] projectionMatrix = new float[16];
            float[] cameraMatrix = new float[16];
            // 设置投影矩阵
            if(imageRatio > viewRatio){
                Matrix.orthoM(projectionMatrix,0,- viewRatio/imageRatio,viewRatio/imageRatio,-1,1,1,3);
            }else{
                Matrix.orthoM(projectionMatrix,0,-1,1,-imageRatio / viewRatio,imageRatio / viewRatio,1,3);
            }
            // 设置相机矩阵
            Matrix.setLookAtM(cameraMatrix,0,0,0,1,0,0,0,0,1,0);
            // 计算mvp矩阵
            Matrix.multiplyMM(mvpMatrix,0,projectionMatrix,0,cameraMatrix,0);
        }
    }
}
