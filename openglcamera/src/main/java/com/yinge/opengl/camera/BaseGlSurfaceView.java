package com.yinge.opengl.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.yinge.opengl.camera.filter.base.gpuimage.GPUImageFilter;
import com.yinge.opengl.camera.filter.helper.FilterFactory;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.util.OpenGlUtils;
import com.yinge.opengl.camera.util.Rotation;
import com.yinge.opengl.camera.util.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 所选择的滤镜，类型为MagicBaseGroupFilter
 * 1.mCameraInputFilter将SurfaceTexture中YUV数据绘制到FrameBuffer
 * 2.filter将FrameBuffer中的纹理绘制到屏幕中
 */
public abstract class BaseGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer{

    // 其他滤镜
    protected GPUImageFilter filter;

    /**
     * SurfaceTexure纹理id
     */
    protected int mTextureId = OpenGlUtils.NO_TEXTURE;

    /**
     * 顶点坐标
     */
    protected final FloatBuffer gLCubeBuffer;

    /**
     * 纹理坐标
     */
    protected final FloatBuffer gLTextureBuffer;

    /**
     * GLSurfaceView的宽高
     */
    protected int surfaceWidth, surfaceHeight;

    /**
     * 图像宽高
     */
    protected int imageWidth, imageHeight;

    protected ScaleType mScaleType = ScaleType.FIT_XY;

    public BaseGlSurfaceView(Context context) {
        this(context, null);
    }

    public BaseGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        surfaceWidth = width;
        surfaceHeight = height;
        onFilterChanged();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 窗口大小改变，传递到 着色器
     */
    protected void onFilterChanged(){
        if(filter != null) {
            filter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
            filter.onInputSizeChanged(imageWidth, imageHeight);
        }
    }

    /**
     * 设置滤镜Filter
     * @param type
     */
    public void setFilter(final FilterType type){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (filter != null) {
                    filter.destroy();
                }
                filter = null;
                filter = FilterFactory.initFilters(type);
                if (filter != null) {
                    filter.init();
                }
                // 滤镜改变
                onFilterChanged();
            }
        });
        requestRender();
    }

    /**
     * 设置滤镜Filter
     * @param newFilter
     */
    public void setFilter(final GPUImageFilter newFilter){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (filter != null) {
                    filter.destroy();
                }
                filter = newFilter;
                if (filter != null) {
                    filter.init();
                }
                // 滤镜改变
                onFilterChanged();
            }
        });
        requestRender();
    }

    protected void deleteTextures() {
        if(mTextureId != OpenGlUtils.NO_TEXTURE){
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    GLES20.glDeleteTextures(1, new int[]{
                            mTextureId
                    }, 0);
                    mTextureId = OpenGlUtils.NO_TEXTURE;
                }
            });
        }
    }

    /**
     * 将当前纹理id，使用filter渲染到纹理，然后读像素，生成新的bitmap
     * 相当于保存滤镜之后的结果
     * @param bitmap
     * @param newTexture
     */
    protected void drawToTexture(final Bitmap bitmap, final boolean newTexture){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                int[] frameBufferObjects = new int[1];
                int[] frameBufferTextures = new int[1];

                GLES20.glGenFramebuffers(1, frameBufferObjects, 0);
                GLES20.glGenTextures(1, frameBufferTextures, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures[0]);

                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                // 绑定Fbo，并附加FBO纹理id
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferObjects[0]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBufferTextures[0], 0);

                GLES20.glViewport(0, 0, width, height);
                if (filter != null) {
                    filter.onInputSizeChanged(imageWidth, imageWidth);
                    filter.onDisplaySizeChanged(width, height);
                }
                int textureId = OpenGlUtils.NO_TEXTURE;
                if (newTexture){
                    textureId = OpenGlUtils.createBitmapTexture(bitmap, true);
                } else {
                    textureId = mTextureId;
                }
                // 开始渲染到fbo
                filter.onDrawFrame(textureId);
                // 从fbo读内存
                IntBuffer intBuffer = IntBuffer.allocate(width * height);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,intBuffer );
                Bitmap bitmapAfterFilter = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmapAfterFilter.copyPixelsFromBuffer(IntBuffer.wrap(intBuffer.array()));

                if (newTexture) {
                    GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
                }

                GLES20.glDeleteFramebuffers(1, frameBufferObjects, 0);
                GLES20.glDeleteTextures(1, frameBufferTextures, 0);
                GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
                filter.destroy();
                filter.init();
                filter.onDisplaySizeChanged(imageWidth, imageHeight);
                // 把渲染后的 bitmap传出去
                getBitmapAfterFiltered(bitmapAfterFilter);
            }
        });
    }

    /**
     * 获取 filter渲染后的bitmap，之前调用{@link #drawToTexture(Bitmap, boolean)}
     * @param bitmapAfterFiltered
     */
    protected void getBitmapAfterFiltered(Bitmap bitmapAfterFiltered) {

    }

    /**
     * 保存图片
     * @param savePictureTask
     */
    public abstract void savePicture(SavePictureTask savePictureTask);

    /**
     * 调整 角度
     * @param rotation
     * @param flipHorizontal
     * @param flipVertical
     */
    protected void adjustSize(int rotation, boolean flipHorizontal, boolean flipVertical){

        float[] textureCords = TextureRotationUtil.getRotation(Rotation.fromInt(rotation), flipHorizontal, flipVertical);

        float[] cube = TextureRotationUtil.CUBE;
        // 宽比
        float ratio1 = (float)surfaceWidth / imageWidth;
        // 高比
        float ratio2 = (float)surfaceHeight / imageHeight;

        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(imageWidth * ratioMax);
        int imageHeightNew = Math.round(imageHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float)surfaceWidth;
        float ratioHeight = imageHeightNew / (float)surfaceHeight;

        if(mScaleType == ScaleType.CENTER_INSIDE){
            cube = new float[]{
                    TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                    TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                    TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                    TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
            };
        }else if(mScaleType == ScaleType.FIT_XY){

        }else if(mScaleType == ScaleType.CENTER_CROP){
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                    addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                    addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                    addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
            };
        }
        gLCubeBuffer.clear();
        gLCubeBuffer.put(cube).position(0);
        gLTextureBuffer.clear();
        gLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public enum  ScaleType{
        CENTER_INSIDE,
        CENTER_CROP,
        FIT_XY;
    }
}
