#version 330

in vec3 position;
in vec4 color;

uniform mat4 projection;
uniform mat4 modelview;

out vec4 vertexColor;

void main(){

    vertexColor = color;



    gl_Position = projection * modelview * vec4(position.x,position.y,position.z,1.0);

}
