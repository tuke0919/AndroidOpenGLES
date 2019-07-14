// 顶点坐标
attribute vec4 vPosition;
// 顶点对应的纹理坐标
attribute vec2 vCoord;
// 顶点变换矩阵
uniform mat4 vMatrix;

// 传给片元着色器的 纹理坐标
varying vec2 aTextureCoordinate;

void main(){
    gl_Position = vMatrix * vPosition;
    aTextureCoordinate = vCoord;
}