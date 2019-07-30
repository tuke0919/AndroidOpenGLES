//
// Created by KE TU on 2019/7/30.
//
#ifndef _BITMAP_OPERATION_H_
#define _BITMAP_OPERATION_H_

#include <jni.h>
#include "JniBitmap.h"

class BitmapOperation {

public:
    static int32_t convertArgbToInt(ARGB argb);
    static void convertIntToArgb(uint32_t pixel, ARGB* argb);

    static jobject native_storeBitmap(JNIEnv* jniEnv, jobject obj, jobject bitmap);
    static void native_freeBitmap(JNIEnv* jniEnv, jobject obj, jobject handle);
    static jobject native_getBitmap(JNIEnv* jniEnv, jobject obj, jobject handle);
};

#endif