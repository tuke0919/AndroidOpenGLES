/*
 *
 * FBOActivity.java
 * 
 * Created by Wuwang on 2016/12/24
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.yinge.opengl.fbo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yinge.opengl.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 *  FBO 离屏渲染
 */
public class FboActivity extends AppCompatActivity implements FboRender.Callback {

    private static final String TAG = "FboActivity";

    private FboRender mRender;
    private ImageView mImage;
    private GLSurfaceView mGLView;

    private int mBmpWidth,mBmpHeight;
    private String mImgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo);
        mGLView= (GLSurfaceView)findViewById(R.id.mGLView);
        mGLView.setEGLContextClientVersion(2);

        mRender=new FboRender(this);
        mRender.setCallback(this);
        mGLView.setRenderer(mRender);

        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mImage= (ImageView)findViewById(R.id.mImage);
    }

    public void onClick(View view){
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            mImgPath = c.getString(columnIndex);
            Log.e(TAG,"img->" + mImgPath);
            Bitmap bmp = BitmapFactory.decodeFile(mImgPath);
            mBmpWidth = bmp.getWidth();
            mBmpHeight = bmp.getHeight();

            // 设置图片，请求 渲染
            mRender.setBitmap(bmp);
            mGLView.requestRender();
            c.close();
        }
    }

    @Override
    public void onCall(final ByteBuffer data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"callback success");
                Bitmap bitmap= Bitmap.createBitmap(mBmpWidth,mBmpHeight, Bitmap.Config.ARGB_8888);
                // 从缓冲区读 二进制缓冲数据
                bitmap.copyPixelsFromBuffer(data);
                // 保存图像
                saveBitmap(bitmap);
                data.clear();
            }
        }).start();
    }

    //图片保存
    public void saveBitmap(final Bitmap b){
        String path = mImgPath.substring(0,mImgPath.lastIndexOf("/")+1);
        File folder = new File(path);
        if(!folder.exists() && !folder.mkdirs()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FboActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake +".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FboActivity.this, "保存成功->"+jpegName, Toast.LENGTH_SHORT).show();
                // 设置图像
                mImage.setImageBitmap(b);
            }
        });

    }

}
