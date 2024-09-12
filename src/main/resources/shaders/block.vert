#version 330

in vec3 position;
in vec4 color;
in vec2 uv;
in vec3 normal;

uniform mat4 projection;
uniform mat4 modelview;

out vec4 vertexColor;
out vec2 fragmentPos;
out vec3 blockNormal;

void main(){


    blockNormal = normal;
    vertexColor = color;
    fragmentPos = uv;

    gl_Position = projection * modelview * vec4(position,1.0);
}

