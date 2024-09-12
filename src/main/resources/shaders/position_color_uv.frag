#version 330

in vec4 vertexColor;
in vec2 uvCoords;

uniform sampler2D sampler0;

out vec4 fragColor;

void main(){

    vec4 col = texture(sampler0,uvCoords);

    fragColor = col * vertexColor;

}