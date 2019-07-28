package com.yinge.opengl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yinge.opengl.camera.CameraActivity;
import com.yinge.opengl.camera.camera.CameraFilterActivity;
import com.yinge.opengl.camerademo.CameraDemoActivity;
import com.yinge.opengl.egl.EGLRenderActivity;
import com.yinge.opengl.fbo.FboActivity;
import com.yinge.opengl.image.GLImageViewActivity;
//import com.yinge.opengl.magic.CameraFilterActivity;
import com.yinge.opengl.render.GLShapeViewActivity;
import com.yinge.opengl.transform.TransformActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mList;
    private ArrayList<MenuBean> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList= (RecyclerView)findViewById(R.id.mList);
        mList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        data=new ArrayList<>();
        add("绘制形体", GLShapeViewActivity.class);
        add("图片处理", GLImageViewActivity.class);
        add("图形变换", TransformActivity.class);
        add("普通相机", CameraActivity.class);
        add("美颜滤镜相机", CameraFilterActivity.class);
        add("相机DEMO", CameraDemoActivity.class);
//        add("相机3 美颜",Camera3Activity.class);
//        add("压缩纹理动画",ZipActivity.class);
        add("FBO使用", FboActivity.class);
        add("EGL渲染", EGLRenderActivity.class);
//        add("3D obj模型",ObjLoadActivity.class);
//        add("obj+mtl模型",ObjLoadActivity2.class);
//        add("VR效果",VrContextActivity.class);
//        add("颜色混合",BlendActivity.class);
//        add("光照",LightActivity.class);
        mList.setAdapter(new MenuAdapter());
    }


    private void add(String name,Class<?> clazz){
        MenuBean bean=new MenuBean();
        bean.name=name;
        bean.clazz=clazz;
        data.add(bean);
    }

    private class MenuBean{

        String name;
        Class<?> clazz;

    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder>{


        @Override
        public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuHolder(getLayoutInflater().inflate(R.layout.item_button,parent,false));
        }

        @Override
        public void onBindViewHolder(MenuHolder holder, int position) {
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MenuHolder extends RecyclerView.ViewHolder{

            private Button mBtn;

            MenuHolder(View itemView) {
                super(itemView);
                mBtn= (Button)itemView.findViewById(R.id.mBtn);
                mBtn.setOnClickListener(MainActivity.this);
            }

            public void setPosition(int position){
                MenuBean bean=data.get(position);
                mBtn.setText(bean.name);
                mBtn.setTag(position);
            }
        }

    }

    @Override
    public void onClick(View view){
        int position= (int)view.getTag();
        MenuBean bean=data.get(position);
        startActivity(new Intent(this,bean.clazz));
    }


}
