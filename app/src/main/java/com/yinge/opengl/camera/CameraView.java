package com.yinge.opengl.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 功能：Camera + SurfaceTexture + GLSurfaceView + OpenGLES
 * </p>
 * <p>Copyright corp.xxx.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/14
 * @email tuke@xxx.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    // 相机管理类
    private KitkatCamera mCamera;
    // GLES 绘图
    private CameraDrawer mCameraDrawer;

    // 相机id
    private int mCameraId = 1;

    private Runnable mRunnable;


    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mCamera = new KitkatCamera();
        mCameraDrawer = new CameraDrawer(getContext());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl,config);

        if(mRunnable!=null){
            mRunnable.run();
            mRunnable=null;
        }

        // 打开相机
        mCamera.open(mCameraId);
        Point previewSize = mCamera.getPreviewSize();

        mCameraDrawer.setCameraId(mCameraId);
        mCameraDrawer.setDataSize(previewSize.x , previewSize.y);

        mCamera.setPreviewTexture(mCameraDrawer.getSurfaceTexture());

        // 图像流设置 图像帧可用 监听器
        mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        // 开始预览
        mCamera.startPreview();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置 视口大小
        mCameraDrawer.setViewSize(width,height);
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 图像流 会多次调用 此方法

        // 真正的绘图
        mCameraDrawer.onDrawFrame(gl);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera(){
        mRunnable=new Runnable() {
            @Override
            public void run() {
                mCamera.stopPreview();

                mCameraId = mCameraId == 1 ? 0 : 1;
            }
        };
        onPause();
        onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }
}
