package com.yinge.opengl.camera.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

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
public class OesFilter extends AbsOesImageFilter {

    // 纹理坐标变换矩阵引用
    private int glCoordMatrixHandle;
    //  纹理坐标变换矩阵 - 单位矩阵
    private float[] mCoordMatrix= Arrays.copyOf(IDENTITY,16);

    public OesFilter(Context context) {
        super(context);
    }

    @Override
    public String getVertexResPath() {
        return "camera/oes_base_vertex.glsl";
    }

    @Override
    public String getFragmentResPath() {
        return "camera/oes_base_fragment.glsl";
    }

    @Override
    public void initOtherHandle() {
        glCoordMatrixHandle = GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
    }

    @Override
    public void onSizeChanged(int width, int height) {

    }

    @Override
    public void setOtherHandle() {
        GLES20.glUniformMatrix4fv(glCoordMatrixHandle,1,false,mCoordMatrix,0);
    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType());
        // 绑定外部纹理id
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, getTextureId());
        GLES20.glUniform1i(glTextureHandle, getTextureType());
    }
}
