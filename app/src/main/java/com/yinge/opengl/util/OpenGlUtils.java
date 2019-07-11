package com.yinge.opengl.util;

import android.content.res.Resources;

import java.io.InputStream;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/7/7
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class OpenGlUtils {

    /**
     * 从文件中 shader 源码
     * @param resources
     * @param shaderName
     * @return
     */
    public static String loadShaderSrcFromAssetFile(Resources resources, String shaderName) {
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=resources.getAssets().open(shaderName);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
//        return result.toString().replaceAll("\\r\\n","\n");
        return result.toString().replaceAll("\\r\\n","");

    }


}
