package com.yinge.opengl.camera.filter;

import android.content.Context;

/**
 * 功能：灰色过滤器
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/14
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class GrayFilter extends AbsOesImageFilter {


    public GrayFilter(Context context) {
        super(context);
    }

    @Override
    public String getVertexResPath() {
        return "camera/base_vertex.glsl";
    }

    @Override
    public String getFragmentResPath() {
        return "camera/gray_fragment.glsl";
    }

    @Override
    public void initOtherHandle() {

    }

    @Override
    public void onSizeChanged(int width, int height) {

    }

    @Override
    public void setOtherHandle() {

    }
}
