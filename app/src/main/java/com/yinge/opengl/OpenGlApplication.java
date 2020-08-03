package com.yinge.opengl;

import android.app.Application;

import com.yinge.opengl.camera.util.OpenGlCameraSdk;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/17
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class OpenGlApplication extends Application {

    private static OpenGlApplication app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        OpenGlCameraSdk.getInstance().init(this);
    }

    public static OpenGlApplication getInstance() {
        return app;
    }


}
