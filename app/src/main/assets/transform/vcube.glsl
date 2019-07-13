// 顶点坐标（物体空间）
attribute vec4 vPosition;
// 总矩阵
uniform mat4 vMatrix;
// 渲染颜色
attribute vec4 aColor;

// 传递给片元 着色器
varying vec4 vColor;
void main() {
    gl_Position = vMatrix * vPosition;
    vColor = aColor;
}
