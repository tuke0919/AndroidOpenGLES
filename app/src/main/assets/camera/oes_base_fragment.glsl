#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 aTextureCoordinate;
// 外部纹理 采样器
uniform samplerExternalOES vTexture;
void main() {
    gl_FragColor = texture2D(vTexture, aTextureCoordinate );
}