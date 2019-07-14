precision mediump float;
varying vec2 aTextureCoordinate;
uniform sampler2D vTexture;
void main() {
    vec4 color = texture2D( vTexture, aTextureCoordinate);
    float rgb = color.g;
    vec4 c = vec4(rgb,rgb,rgb,color.a);
    gl_FragColor = c;
}