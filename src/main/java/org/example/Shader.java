package org.example;
import org.example.util.FileUtil;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;


import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;


public class Shader {

    private int shaderProgramId;
    private int vertexShader;
    private int fragmentShader;

    private String shaderName;

    public Shader(String name, VertexFormat format){
        this.shaderName = name;
        String vertex = FileUtil.readPrimitiveFileFromResources(name + ".vert");
        String fragment = FileUtil.readPrimitiveFileFromResources(name + ".frag");

        shaderProgramId = glCreateProgram();
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        this.compileVertexShader(vertex);
        this.compileFragmentShader(fragment);

        for (int i = 0; i < format.elements.size();i++){
            glBindAttribLocation(shaderProgramId,i,format.elements.get(i).name);
        }

        this.link();
        this.validate();
    }

    private void link(){
        glAttachShader(shaderProgramId,vertexShader);
        glAttachShader(shaderProgramId,fragmentShader);
        glLinkProgram(shaderProgramId);
        int status = glGetProgrami(shaderProgramId,GL_LINK_STATUS);
        if (status == GL_FALSE){
            String log = glGetProgramInfoLog(shaderProgramId);
            System.err.println(log);
            throw new RuntimeException("Couldn't link program " + this.shaderName);
        }
    }
    private void validate(){
        glValidateProgram(shaderProgramId);
        int status = glGetProgrami(shaderProgramId,GL_VALIDATE_STATUS);
        if (status == GL_FALSE){
            String log = glGetProgramInfoLog(shaderProgramId);
            System.err.println(log);
            throw new RuntimeException("Couldn't validate program " + this.shaderName);
        }
    }
    private void compileVertexShader(String src){
        glShaderSource(vertexShader,src);
        glCompileShader(vertexShader);

        int status = glGetShaderi(vertexShader,GL_COMPILE_STATUS);
        if (status == GL_FALSE){
            String log = glGetShaderInfoLog(vertexShader);
            System.err.println(log);
            throw new RuntimeException("Couldn't compile shader " + this.shaderName);
        }
    }
    private void compileFragmentShader(String src){
        glShaderSource(fragmentShader,src);
        glCompileShader(fragmentShader);

        int status = glGetShaderi(fragmentShader,GL_COMPILE_STATUS);
        if (status == GL_FALSE){
            String log = glGetShaderInfoLog(fragmentShader);
            System.err.println(log);
            throw new RuntimeException("Couldn't compile shader " + this.shaderName);
        }
    }

    public int getShaderProgramId() {
        return shaderProgramId;
    }

    public void run(){
        glUseProgram(shaderProgramId);
    }
    public void run(Matrix4f projection,Matrix4f modelview){
        glUseProgram(shaderProgramId);
        mat4Uniform("projection",projection);
        mat4Uniform("modelview",modelview);
    }

    public void clear(){
        glUseProgram(0);
    }

    public void mat4Uniform(String name, Matrix4f mat){
        int location = getUniformLocation(name);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(16);
        mat.get(buffer);
        glUniformMatrix4fv(location,false,buffer);
        MemoryUtil.memFree(buffer);
    }

    public void samplerUniform(int texid){
        int location = getUniformLocation("sampler" + texid);
        glUniform1i(location,texid);
    }

    private int getUniformLocation(String name){
        int loc = glGetUniformLocation(shaderProgramId,name);
        if (loc == -1){
            throw new RuntimeException("Cannot find uniform with name: " + name + " in shader: " + this.shaderName);
        }
        return loc;
    }
}