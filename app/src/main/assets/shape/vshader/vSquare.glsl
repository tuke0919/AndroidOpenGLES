attribute vec4 vPosition;
uniform mat4 vMatrix;
void main() {
    gl_Position = vMatrix * vPosition;
}
