precision mediump float;

// 采样器
uniform sampler2D vTexture;
// 类型
uniform int vChangeType;
// 类型颜色
uniform vec3 vChangeColor;
// 是否处理一半
uniform int vIsHalf;
//
uniform float uXY;

// 接收顶点传来 的 纹理坐标
varying vec2  aCoordinate;
// 接收顶点传来 的 顶点坐标（物体空间坐标）
varying vec4 aPos;
// 接收顶点传来 的 变换后的坐标（裁剪空间坐标）
varying vec4 gPosition;

void main() {
    // 根据纹理坐标 采颜色
    vec4 nColor = texture2D(vTexture, aCoordinate);

    if(aPos.x > 0.0 || vIsHalf == 0) {
        if(vChangeType == 1) {
            // 灰色
            float gray = nColor.r * vChangeColor.r + nColor.g * vChangeColor.g + nColor.b *vChangeColor.b;
            gl_FragColor = vec4(gray, gray, gray, nColor.a);

        } else if(vChangeType == 2) {
            // 冷色调 和 暖色调
            vec4 deltaColor = nColor + vec4(vChangeColor, 0.0);
            deltaColor.r = max(min(deltaColor.r, 1.0), 0.0);
            deltaColor.g = max(min(deltaColor.g, 1.0), 0.0);
            deltaColor.b = max(min(deltaColor.b, 1.0), 0.0);
            deltaColor.a = max(min(deltaColor.a, 1.0), 0.0);
            gl_FragColor = deltaColor;

        } else if(vChangeType == 3) {
            // 高斯模糊
            // r 的左上右下点
            nColor += texture2D(vTexture, vec2(aCoordinate.x - vChangeColor.r, aCoordinate.y - vChangeColor.r));
            nColor += texture2D(vTexture, vec2(aCoordinate.x - vChangeColor.r, aCoordinate.y + vChangeColor.r));
            nColor += texture2D(vTexture, vec2(aCoordinate.x + vChangeColor.r, aCoordinate.y - vChangeColor.r));
            nColor += texture2D(vTexture, vec2(aCoordinate.x + vChangeColor.r, aCoordinate.y + vChangeColor.r));

            // g 的左上右下点
            nColor += texture2D(vTexture, vec2(aCoordinate.x - vChangeColor.g, aCoordinate.y - vChangeColor.g));
            nColor += texture2D(vTexture, vec2(aCoordinate.x - vChangeColor.g, aCoordinate.y + vChangeColor.g));
            nColor += texture2D(vTexture, vec2(aCoordinate.x + vChangeColor.g, aCoordinate.y - vChangeColor.g));
            nColor += texture2D(vTexture, vec2(aCoordinate.x + vChangeColor.g, aCoordinate.y + vChangeColor.g));


            // b 的左上右下点
            nColor += texture2D(vTexture, vec2(aCoordinate.x - vChangeColor.b, aCoordinate.y - vChangeColor.b));
            nColor += texture2D(vTexture, vec2(aCoordinate.x - vChangeColor.b, aCoordinate.y + vChangeColor.b));
            nColor += texture2D(vTexture, vec2(aCoordinate.x + vChangeColor.b, aCoordinate.y - vChangeColor.b));
            nColor += texture2D(vTexture, vec2(aCoordinate.x + vChangeColor.b, aCoordinate.y + vChangeColor.b));

            // 加上本生的点，共13点平均
            nColor /= 13.0;
            gl_FragColor = nColor;

        } else if(vChangeType == 4) {
            // 放大镜
            float dis = distance(vec2(gPosition.x, gPosition.y / uXY), vec2(vChangeColor.r, vChangeColor.g));
            if(dis < vChangeColor.b){
                nColor=texture2D(vTexture,vec2(aCoordinate.x/2.0 + 0.25, aCoordinate.y/2.0 + 0.25));
            }
            gl_FragColor=nColor;

        } else {
            gl_FragColor = nColor;
        }
    } else {
        gl_FragColor = nColor;
    }
}
