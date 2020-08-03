package com.yinge.opengl.camera.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.yinge.opengl.camera.BaseGlSurfaceView;
import com.yinge.opengl.camera.SavePictureTask;
import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.util.OpenGlUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/28
 * @email tuke@corp.xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ImageGlSurfaceView extends BaseGlSurfaceView {

    // 图像输入过滤器(就是做没有任何处理的)
    private final GPUImageFilter mImageInputFilter;

    private Bitmap mOriginBitmap;

    public ImageGlSurfaceView(Context context) {
        super(context);
        mImageInputFilter = new GPUImageFilter();
    }

    public ImageGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mImageInputFilter = new GPUImageFilter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mImageInputFilter.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        // 调整图像，要先设置 图像宽高
        adjustSize(0, false, false);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if(mTextureId == OpenGlUtils.NO_TEXTURE) {
            mTextureId = OpenGlUtils.createBitmapTexture(getBitmap(), true);
        }
        if(filter == null) {
            mImageInputFilter.onDrawFrame(mTextureId, gLCubeBuffer, gLTextureBuffer);
        } else {
            filter.onDrawFrame(mTextureId, gLCubeBuffer, gLTextureBuffer);
        }

    }

    /**
     * @param bitmap
     */
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        setBitmap(bitmap);
        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
        adjustSize(0, false, false);
        requestRender();
    }

    public void setBitmap(Bitmap bitmap){
        mOriginBitmap = bitmap;
    }

    /**
     * 保存图像滤镜修改
     */
    public void saveImageFilterModify() {
        if (filter != null) {
            drawToTexture(mOriginBitmap, false);
            deleteTextures();
            setFilter(FilterType.NONE);
        }
    }

    /**
     * 保存修改后的图像
     * @param bitmapAfterFiltered
     */
    @Override
    protected void getBitmapAfterFiltered(Bitmap bitmapAfterFiltered) {
        mOriginBitmap = bitmapAfterFiltered;
        requestRender();
    }

    public void freeBitmap(){

    }

    public Bitmap getBitmap(){
        return mOriginBitmap;
    }


    @Override
    public void savePicture(SavePictureTask savePictureTask) {

    }

    public void onDestroy(){
        if (mOriginBitmap != null) {
            mOriginBitmap.recycle();
            mOriginBitmap = null;
        }
    }
}
