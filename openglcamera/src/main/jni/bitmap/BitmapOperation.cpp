//
// Created by KE TU on 2019/7/30.
//
#include <cstdint>
#include <android/log.h>
#include "BitmapOperation.h"
#include <cstring>

#define  LOG_TAG    "BitmapOperation"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

int32_t BitmapOperation::convertArgbToInt(ARGB argb) {
    return (argb.alpha << 24) | (argb.red << 16) | (argb.green << 8) | argb.blue;
}

void BitmapOperation::convertIntToArgb(uint32_t pixel, ARGB *argb) {
    argb -> alpha = (pixel >> 24);
    argb -> red = ((pixel >> 16) & 0xff);
    argb -> green = ((pixel >> 8) & 0xff);
    argb -> blue = (pixel & 0xff);

}

jobject BitmapOperation::native_storeBitmap(JNIEnv *jniEnv, jobject obj, jobject bitmap) {
    LOGD("reading bitmap info...");
    AndroidBitmapInfo bitmapInfo;
    uint32_t * storedBitmapPixels = NULL;

    // 获取bitmap的信息
    int result;
    if ((result == AndroidBitmap_getInfo(jniEnv, bitmap, &bitmapInfo)) < 0){
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", result);
        return NULL;
    }
    LOGD("width:%d height:%d stride:%d", bitmapInfo.width, bitmapInfo.height, bitmapInfo.stride);
    if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return NULL;
    }

    // 读bitmap到native内存
    void* bitmapPixels;
    if ((result == AndroidBitmap_lockPixels(jniEnv, bitmap, &bitmapPixels)) < 0){
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", result);
        return NULL;
    }
    // native中原始bitmap地址
    uint32_t * src = static_cast<uint32_t *>(bitmapPixels);
    // 目的bitmap的地址
    storedBitmapPixels = new uint32_t[bitmapInfo.height * bitmapInfo.width];

    int pixelCount = bitmapInfo.width * bitmapInfo.height;
    // 内存拷贝
    memcpy(storedBitmapPixels, src, sizeof(uint32_t) * pixelCount);

    AndroidBitmap_unlockPixels(jniEnv, bitmap);

    // 将像素起始地址 和 信息保存在自定义的一个类
    JniBitmap* jniBitmap = new JniBitmap();
    jniBitmap -> _storedBitmapPixels = storedBitmapPixels;
    jniBitmap -> _bitmapInfo = bitmapInfo;

    LOGD("return NewDirectByteBuffer");

    return jniEnv -> NewDirectByteBuffer(jniBitmap, 0);
}

void BitmapOperation::native_freeBitmap(JNIEnv *jniEnv, jobject obj, jobject handle) {
     JniBitmap * jniBitmap = static_cast<JniBitmap *>(jniEnv->GetDirectBufferAddress(handle));
     if (jniBitmap -> _storedBitmapPixels == NULL) {
         return;
     }
     // 删除像素内存
     delete [] jniBitmap -> _storedBitmapPixels;
     jniBitmap -> _storedBitmapPixels = NULL;
     // 删除这个对象地址
     delete jniBitmap;

}

jobject BitmapOperation::native_getBitmap(JNIEnv *jniEnv, jobject obj, jobject handle) {
    JniBitmap * jniBitmap = static_cast<JniBitmap *>(jniEnv ->GetDirectBufferAddress(handle));
    if (jniBitmap ->_storedBitmapPixels == NULL){
        LOGD("no bitmap data was stored. returning null...");
        return NULL;
    }

    /**
     *
     * 1.creating a new bitmap to put the pixels into it -
     * using Bitmap Bitmap.createBitmap (int width, int height, Bitmap.Config config) :
     */

    jclass bitmapCls = jniEnv->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethod = jniEnv->GetStaticMethodID(bitmapCls,
            "createBitmap",
            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");


    jstring configName = jniEnv->NewStringUTF("ARGB_8888");

    jclass bitmapConfigClass = jniEnv->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = jniEnv->GetStaticMethodID(
            bitmapConfigClass,
            "valueOf",
            "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    // java 中的 Config 对象
    jobject bitmapConfig = jniEnv->CallStaticObjectMethod(bitmapConfigClass,
                                                       valueOfBitmapConfigFunction,
                                                       configName);

    // 生成新的bitmap, 此时是空白的
    jobject newBitmap = jniEnv->CallStaticObjectMethod(bitmapCls,
            createBitmapMethod,
            jniBitmap->_bitmapInfo.width,
            jniBitmap->_bitmapInfo.height,
            bitmapConfig);

    /**
     *
     * 2.putting the pixels into the new bitmap:
     *
     * */

    int result;
    void * bitmapPixels;
    // 获得bitmap的native地址
    if ((result == AndroidBitmap_lockPixels(jniEnv, newBitmap, &bitmapPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", result);
        return NULL;
    }
    // 新的位图的 地址
    uint32_t * newBitmapPixels = (uint32_t*) bitmapPixels;
    int pixelsCount = jniBitmap->_bitmapInfo.height * jniBitmap->_bitmapInfo.width;
    // 内存拷贝，把原来内存的地址 拷贝 到新的位图的地址
    memcpy(newBitmapPixels, jniBitmap->_storedBitmapPixels, sizeof(uint32_t) * pixelsCount);
    AndroidBitmap_unlockPixels(jniEnv, newBitmap);

    // 返回
    return newBitmap;
}
