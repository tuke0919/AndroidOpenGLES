package com.yinge.opengl.camera.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


import com.yinge.opengl.camera.BaseGlSurfaceView;
import com.yinge.opengl.camera.SavePictureTask;
import com.yinge.opengl.camera.filter.helper.FilterType;
import com.yinge.opengl.camera.util.ImageUtil;
import com.yinge.opengl.camera.util.OpenGlUtils;
import com.yinge.opengl.camera.util.Rotation;
import com.yinge.opengl.camera.util.TextureRotationUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.yinge.opengl.camera.camera.CameraProxy.TAG;

/**
 * 相机预览 + 美颜 + 滤镜 + 拍照 其实有三个过程：
 * 第一：相机预览的实现：Camera + GLSurfaceView ,需要将 MagicCameraInputFilter，将SurfaceTexture中YUV数据直接绘制窗口
 * 第二：美颜等级选择，是直接设置 MagicCameraInputFilter 的着色器，对Camera传来的图像像素 进行美颜的
 * 第三：加了滤镜Filter后，实际是 MagicCameraInputFilter 里的美颜 和 滤镜Filter 的叠加渲染，这时候需要用到 FBO，
 *      MagicCameraInputFilter先输出到FBO的纹理FBOTextureId，滤镜Filter 在拿FBOTextureId渲染 ，最后解绑输出到屏幕的默认帧缓冲，显示在WindowSurface上
 * 第四：拍照：其实拍照时，回调出来的依然是 原始图像数据bitmap(未经过美颜滤镜渲染的)，如果预览时 有美颜 + 滤镜Filter，需要重新拿 "美颜 + 滤镜Filter" 渲染这个bitmap
 *
 */
public class CameraGlSurfaceView extends BaseGlSurfaceView {

    // 相机输入着色器
    private MagicCameraInputFilter mCameraInputFilter;
    // 拍照时美颜图片
    private MagicBeautyFilter mBeautyFilter;

    // 接收相机的图像流
    private SurfaceTexture mSurfaceTexture;

    // 相机
    private ICamera mCameraManger;

    private boolean mIsRecording;

    public CameraGlSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCameraManger = getCamera(context);
        this.getHolder().addCallback(this);
        mScaleType = ScaleType.CENTER_CROP;
    }

    /**
     * 返回相机
     * @param context
     * @return
     */
    public ICamera getCamera(Context context) {
        return new CameraProxy(context, false);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        if(mCameraInputFilter == null) {
            mCameraInputFilter = new MagicCameraInputFilter();
        }
        // 创建主程序，获取引用等
        mCameraInputFilter.init();

        if (mTextureId == OpenGlUtils.NO_TEXTURE) {
            // 获取扩展纹理id
            mTextureId = OpenGlUtils.getExternalOESTextureID();

            if (mTextureId != OpenGlUtils.NO_TEXTURE) {
                // 创建SurfaceTexture
                mSurfaceTexture = new SurfaceTexture(mTextureId);
                // 设置 图像帧可用监听器
                mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        // 当图像帧 可用时 SurfaceTexture把这帧图像写入 mTextureId，此处调用onDrawFrame(GL10 gl)绘图
                        requestRender();
                    }
                });
            }
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        // 打开相机
        openCamera();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if(mSurfaceTexture == null) {
            return;
        }
        // 更新图片
        mSurfaceTexture.updateTexImage();

        // 获取并设置纹理变换矩阵
        float[] textureMatrix = new float[16];
        mSurfaceTexture.getTransformMatrix(textureMatrix);
        mCameraInputFilter.setTextureTransformMatrix(textureMatrix);

        int id = mTextureId;
        if(filter == null){
            // 没有使用FBO，直接绘制到窗口默认帧缓冲上，然后到windowSurface
            mCameraInputFilter.onDrawFrame(mTextureId, gLCubeBuffer, gLTextureBuffer);
        }else{
            // 先把纹理渲染到FBO的 颜色附着点，然后取 渲染后的纹理id
            id = mCameraInputFilter.onDrawToTexture(mTextureId);
            // 再次 经过滤镜filter渲染到屏幕上
            filter.onDrawFrame(id, gLCubeBuffer, gLTextureBuffer);
        }
    }

    @Override
    public void setFilter(FilterType type) {
        super.setFilter(type);
    }

    /**
     * 打开相机
     */
    private void openCamera(){

        mCameraManger.openCamera();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraManger.getCameraId(), cameraInfo);
        int orientation = cameraInfo.orientation;

        if( orientation == 90 || orientation == 270){
            imageWidth = mCameraManger.getPreviewSize().height;
            imageHeight = mCameraManger.getPreviewSize().width;
        }else{
            imageWidth = mCameraManger.getPreviewSize().width;
            imageHeight = mCameraManger.getPreviewSize().height;
        }

        mCameraInputFilter.onInputSizeChanged(imageWidth, imageHeight);

        // 调整方向
        adjustSize(orientation, mCameraManger.getCameraId() == 1 , true);

        // 设置图像流 开始预览
        if(mSurfaceTexture != null) {
            mCameraManger.startPreview(mSurfaceTexture);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        // 释放相机资源
        if (mCameraManger != null) {
            mCameraManger.releaseCamera();
        }
    }


    /**
     * 窗口 大小变化
     */
    @Override
    protected void onFilterChanged(){
        super.onFilterChanged();
        mCameraInputFilter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
        if(filter != null) {
            mCameraInputFilter.initCameraFrameBuffer(imageWidth, imageHeight);
        } else {
            mCameraInputFilter.destroyFrameBuffers();
        }

    }

    @Override
    public void savePicture(final SavePictureTask savePictureTask) {
        mCameraManger.takePicture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // 先停止，还有Surface等资源
                mCameraManger.stopPreview();
                // 创建位图，此位图是相机底层，原始的为经过滤镜，美颜的图像
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (bitmap != null) {
//                            new SavePictureTask(getOutputMediaFile(), null, true).execute(bitmap);
//                        }
//                    }
//                }).start();
                // 旋转相机传感器图像
                final Bitmap rotateBitmap = ImageUtil.rotateBitmap(bitmap, mCameraManger.getPictureRotation(), mCameraManger.isFrontCamera(), false);

                // 加到渲染线程里，其实 自定义一个线程也可以
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        // 重新用之前的filter，进行渲染
                        final Bitmap photo = drawPhoto(rotateBitmap, mCameraManger.isFrontCamera());
                        GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
                        if (photo != null) {
                            savePictureTask.execute(photo);
                        }
                    }
                });
                // 在打开
                mCameraManger.startPreview(mSurfaceTexture);
            }
        });
    }

    /**
     *
     * 用目前 预览相机的 filter，去重新绘制 拍照生成的没有处理过的 bitmap
     * @param bitmap
     * @param isRotated
     * @return
     */
    private Bitmap drawPhoto(Bitmap bitmap,boolean isRotated){
        // 原图 宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // ########此处的方法时 渲染到FBO  ########

        // FBO对象
        int[] mFrameBufferObjects = new int[1];
        // FBO上的纹理id
        int[] mFrameBufferTextureIds = new int[1];

        // 初始化美颜filter 过程
        if(mBeautyFilter == null) {
            mBeautyFilter = new MagicBeautyFilter();
        }
        mBeautyFilter.init();
        mBeautyFilter.onDisplaySizeChanged(width, height);
        mBeautyFilter.onInputSizeChanged(width, height);

        // 其他滤镜Filter
        if(filter != null) {
            filter.onInputSizeChanged(width, height);
            filter.onDisplaySizeChanged(width, height);
        }

        // 创建并绑定 FBO
        GLES20.glGenFramebuffers(1, mFrameBufferObjects, 0);
        GLES20.glGenTextures(1, mFrameBufferTextureIds, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextureIds[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferObjects[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mFrameBufferTextureIds[0], 0);

        // 窗口大小
        GLES20.glViewport(0, 0, width, height);
//        int textureId = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, true);
        // 创建纹理id，此时有图像数据
        int textureId = OpenGlUtils.createBitmapTexture(bitmap, true);

        // 准备顶点坐标和纹理坐标数据
        FloatBuffer gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        FloatBuffer gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        if(isRotated) {
            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
        } else {
            gLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
        }

        if(filter == null){
            // 没有滤镜Filter, 就只渲染美颜Filter
            mBeautyFilter.onDrawFrame(textureId, gLCubeBuffer, gLTextureBuffer);
        } else {
            // 有滤镜Filter。滤镜 + 美颜都会渲染

            // 先渲染有图像数据的纹理id，到之前附着FBO的纹理id的内存 mFrameBufferTextureIds[0]
            mBeautyFilter.onDrawFrame(textureId);
            // 然后 在拿FBO的纹理id 内存地址，接着渲染
            filter.onDrawFrame(mFrameBufferTextureIds[0], gLCubeBuffer, gLTextureBuffer);
        }

        // 读取FBO的渲染后的 像素
        IntBuffer ib = IntBuffer.allocate(width * height);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ib);

        // 解绑 FBO和纹理id
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
        GLES20.glDeleteFramebuffers(mFrameBufferObjects.length, mFrameBufferObjects, 0);
        GLES20.glDeleteTextures(mFrameBufferTextureIds.length, mFrameBufferTextureIds, 0);

        // 销毁
        mBeautyFilter.destroy();
        mBeautyFilter = null;
        if(filter != null) {
            filter.onDisplaySizeChanged(surfaceWidth, surfaceHeight);
            filter.onInputSizeChanged(imageWidth, imageHeight);
        }
        return result;
    }

    /**
     * 通知美颜等级改变
     */
    public void onBeautyLevelChanged() {
        mCameraInputFilter.onBeautyLevelChanged();
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mCameraManger.switchCamera();
        mCameraManger.startPreview(mSurfaceTexture);
    }

    /**
     * @return 输出文件路径
     */
    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MagicCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        String timeStamp = System.currentTimeMillis() + "";
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    /**
     * 开始录视频
     */
    public void startRecord() {
        mIsRecording = true;
    }

    /**
     * 结束录视频
     */
    public void stopRecord() {
        mIsRecording = false;
    }

    /**
     * 聚焦，缩放功能
     */
    private float mOldDistance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            // 点击聚焦
            mCameraManger.focusOnPoint((int) event.getX(), (int) event.getY(), getWidth(), getHeight());
            return true;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDistance = getFingerSpacing(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float newDistance = getFingerSpacing(event);
                if (newDistance > mOldDistance) {
                    // 放大
                    mCameraManger.handleZoom(true);
                } else if (newDistance < mOldDistance) {
                    // 缩小
                    mCameraManger.handleZoom(false);
                }
                mOldDistance = newDistance;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 两个手指的距离
     * @param event
     * @return
     */
    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
