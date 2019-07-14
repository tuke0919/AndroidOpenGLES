#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 aTextureCoordinate;
// 外部纹理 采样器
uniform samplerExternalOES vTexture;
void main() {
    // 在赋值给gl_FragColor之前，对color进行更改，做到实时滤镜
    gl_FragColor = texture2D(vTexture, aTextureCoordinate );
}