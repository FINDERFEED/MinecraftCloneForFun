#version 330

in vec3 position;
in vec4 color;
in vec2 uv;

uniform mat4 projection;
uniform mat4 modelview;

out vec4 vertexColor;
out vec2 uvCoords;

void main(){

    vertexColor = color;
    uvCoords = uv;

    gl_Position = projection * modelview * vec4(position.x,position.y,position.z,1.0);

}
