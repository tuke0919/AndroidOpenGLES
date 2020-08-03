/*
 *
 * EGLBackEnvActivity.java
 * 
 * Created by Wuwang on 2017/2/18
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.yinge.opengl.egl;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yinge.opengl.R;
import com.yinge.opengl.camera.filter.GrayFilter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * EGL环境 + 着色器 后台渲染图片
 */
public class EGLRenderActivity extends AppCompatActivity {

    private static String TAG = EGLRenderActivity.class.getSimpleName();
    private ImageView mImage;

    private int mBmpWidth;
    private int mBmpHeight;
    private String mImgPath;
    private EGLRender mEGLRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);
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
            String[] filePathColumns = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
            mImgPath = cursor.getString(columnIndex);

            Log.e(TAG,"img -> " + mImgPath);

            Bitmap bmp= BitmapFactory.decodeFile(mImgPath);
            mBmpWidth=bmp.getWidth();
            mBmpHeight=bmp.getHeight();

            mEGLRender = new EGLRender(mBmpWidth,mBmpHeight);

            mEGLRender.setThreadOwner(getMainLooper().getThread().getName());
            mEGLRender.setFilter(new GrayFilter(this));
            mEGLRender.setBitmap(bmp);

            // 保存图像
            saveBitmap(mEGLRender.getBitmap());
            cursor.close();
        }
    }


    /**
     * 图片保存
     * @param b
     */
    public void saveBitmap(final Bitmap b){
        String path = mImgPath.substring(0,mImgPath.lastIndexOf("/")+1);
        File folder = new File(path);
        if(!folder.exists() && !folder.mkdirs()){
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EGLRenderActivity.this, "无法保存照片", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EGLRenderActivity.this, "保存成功 -> " + jpegName, Toast.LENGTH_SHORT).show();
                mImage.setImageBitmap(b);
            }
        });

    }

}
