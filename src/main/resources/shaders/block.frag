#version 330

uniform sampler2D sampler0;

in vec4 vertexColor;
in vec2 fragmentPos;
in vec3 blockNormal;

out vec4 fragColor;

float lightCoefficient(){
    vec3 light = vec3(0,-1,0);
    float d = (-dot(light,blockNormal) + 1) / 2;
    d = sqrt(d);
    float minLight = 0.4;

    return clamp(d,minLight,1);
}

void main(){




    vec4 col = texture(sampler0,fragmentPos) * vertexColor;

    float l = lightCoefficient();

    col.rgb *= l;

    fragColor =  col;

}