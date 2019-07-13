// 顶点坐标
attribute vec4 vPosition;
// 顶点纹理坐标
attribute vec2 vCoordinate;
// 总变换矩阵
uniform mat4 vMatrix;

// 传递给片元 的 纹理坐标
varying vec2  aCoordinate;
// 传递给片元 的 顶点坐标
varying vec4 aPos;
// 传递给片元 的 变换后的坐标
varying vec4 gPosition;

void main() {
    gl_Position = vMatrix * vPosition;

    aPos = vPosition;
    aCoordinate = vCoordinate;
    gPosition = vMatrix * vPosition;
}
