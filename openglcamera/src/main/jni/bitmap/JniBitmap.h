//
// Created by KE TU on 2019/7/30.
//

#ifndef ANDROIDOPENGLES_JNIBITMAP_H
#define ANDROIDOPENGLES_JNIBITMAP_H

#include <android/bitmap.h>

// 单个pixel的值
typedef struct {

    uint8_t alpha;
    uint8_t red;
    uint8_t green;
    uint8_t blue;

} ARGB;

class JniBitmap {

public:
    // bitmap像素的地址
    uint32_t * _storedBitmapPixels;
    // bitmap的info
    AndroidBitmapInfo _bitmapInfo;

    JniBitmap() {
        _storedBitmapPixels = NULL;
    };
};

#endif //ANDROIDOPENGLES_JNIBITMAP_H