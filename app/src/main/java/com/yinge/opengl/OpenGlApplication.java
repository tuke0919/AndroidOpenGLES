package com.yinge.opengl;

import android.app.Application;

import com.yinge.opengl.camera.util.OpenGlCameraSdk;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/17
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class OpenGlApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OpenGlCameraSdk.getInstance().init(this);
    }
}
