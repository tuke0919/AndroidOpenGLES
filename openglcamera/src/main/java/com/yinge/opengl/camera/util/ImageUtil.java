package com.yinge.opengl.camera.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/26
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ImageUtil {

    private static final String TAG = "ImageUtil";

    /**
     * @param source
     * @param degree
     * @param flipHorizontal
     * @param recycle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap source, int degree, boolean flipHorizontal, boolean recycle) {
//        if (degree == 0 && !flipHorizontal) {
//            return source;
//        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if (flipHorizontal) {
            matrix.postScale(-1, 1);
        }
        Log.d(TAG, "source width: " + source.getWidth() + ", height: " + source.getHeight());
        Log.d(TAG, "rotateBitmap: degree: " + degree);
        Bitmap rotateBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        Log.d(TAG, "rotate width: " + rotateBitmap.getWidth() + ", height: " + rotateBitmap.getHeight());
        if (recycle) {
            source.recycle();
        }
        return rotateBitmap;
    }
}
