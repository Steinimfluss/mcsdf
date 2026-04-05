#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main() {
    vec3 msdf = texture(Sampler0, texCoord0).rgb;

    float sd = median(msdf.r, msdf.g, msdf.b);

    float dist = sd - 0.5;

    float width = fwidth(dist);

    float alpha = smoothstep(-width, width, dist);

    if (alpha <= 0.0) {
        discard;
    }

    vec4 baseColor = vertexColor * ColorModulator;
    fragColor = vec4(baseColor.rgb, baseColor.a * alpha);
}
