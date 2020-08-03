package com.yinge.opengl.image.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/13
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ContrastColorFilter extends AbsImageFilter {

    private Filter mFilter;

    private int mChangeTypeHandle;
    private int mChangeColorHandle;

    public ContrastColorFilter(Context mContext, Filter filter) {
        super(mContext, "image/half_color_vertex.glsl", "image/half_color_fragment.glsl");
        this.mFilter = filter;

    }

    @Override
    public void onGetOtherHandle(int mProgram) {
        mChangeTypeHandle = GLES20.glGetUniformLocation(mProgram, "vChangeType");
        mChangeColorHandle = GLES20.glGetUniformLocation(mProgram, "vChangeColor");
    }

    @Override
    public void onSetOtherHandle() {
       GLES20.glUniform1i(mChangeTypeHandle, mFilter.getType());
       GLES20.glUniform3fv(mChangeColorHandle, 1, mFilter.data(), 0);
    }


    public enum Filter{

        NONE(0,new float[]{0.0f,0.0f,0.0f}),
        GRAY(1,new float[]{0.299f,0.587f,0.114f}),
        COOL(2,new float[]{0.0f,0.0f,0.1f}),
        WARM(2,new float[]{0.1f,0.1f,0.0f}),
        BLUR(3,new float[]{0.006f,0.004f,0.002f}),
        MAGN(4,new float[]{0.0f,0.0f,0.4f});

        private int vChangeType;
        private float[] data;

        Filter(int vChangeType,float[] data){
            this.vChangeType=vChangeType;
            this.data=data;
        }

        public int getType(){
            return vChangeType;
        }

        public float[] data(){
            return data;
        }

    }
}
