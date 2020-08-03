package com.yinge.opengl.camera.camera;

import android.content.Context;
import android.opengl.GLES20;

import com.yinge.opengl.camera.R;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.util.OpenGlCameraSdk;
import com.yinge.opengl.camera.util.OpenGlUtils;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/16
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class MagicBeautyFilter extends GPUImageFilter {

    private int mSingleStepOffsetLocation;
    private int mParamsLocation;

    public MagicBeautyFilter(){
        super(NO_FILTER_VERTEX_SHADER ,
                OpenGlUtils.readShaderFromRawResource ( R.raw.camera_photo_beauty_frag));
    }

    protected void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mParamsLocation = GLES20.glGetUniformLocation(getProgram(), "params");
        setBeautyLevel(OpenGlCameraSdk.getInstance().getBeautyLevel());
    }



    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        setTexelSize(width, height);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[] {2.0f / w, 2.0f / h});
    }

    public void setBeautyLevel(int level){
        switch (level) {
            case 1:
                setFloat(mParamsLocation, 1.0f);
                break;
            case 2:
                setFloat(mParamsLocation, 0.8f);
                break;
            case 3:
                setFloat(mParamsLocation,0.6f);
                break;
            case 4:
                setFloat(mParamsLocation, 0.4f);
                break;
            case 5:
                setFloat(mParamsLocation,0.33f);
                break;
            default:
                break;
        }
    }

    public void onBeautyLevelChanged(){
        setBeautyLevel(OpenGlCameraSdk.getInstance().getBeautyLevel());
    }
}
