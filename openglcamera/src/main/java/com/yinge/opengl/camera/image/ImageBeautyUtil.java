package com.yinge.opengl.camera.image;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/29
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ImageBeautyUtil {
    static {
        System.loadLibrary("ImageBeauty");
    }



    private native void native_storeBitmap(Bitmap bitmap);
    private native void native_freeBitmap(ByteBuffer buffer);
    private native Bitmap native_getBitmap(ByteBuffer buffer);

    private native void native_initBeauty(ByteBuffer buffer);
    private native void native_freeBeauty();

}
