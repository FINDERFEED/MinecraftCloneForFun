package com.finderfeed;

import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class VertexBuffer implements AutoCloseable{

    public int vao;
    public int vbo;
    public int ebo;

    private ByteBuffer buffer;
    private VertexFormat format;
    private int currentElement = 0;
    public boolean vaoReady = false;
    public int vertexCount = 0;

    public VertexBuffer(int bytes,VertexFormat initialDrawFormat){
        this.buffer = MemoryUtil.memAlloc(bytes);
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();
        this.ebo = glGenBuffers();
        this.format = initialDrawFormat;
    }


    public void setFormat(VertexFormat format) {
        this.format = format;
        this.reset();
    }


    /**
     * Only fudging quads 'cause it's a clone for fun
     */
    public void draw(boolean clearOnDraw){
        if (vertexCount % 4 != 0){
            throw new RuntimeException("Incomplete buffer!");
        }
        glBindVertexArray(vao);
        this.loadVBO(clearOnDraw);
        this.loadEBO(clearOnDraw);
        this.initiateVAO(clearOnDraw);

        glDrawElements(GL_TRIANGLES,this.vertexCount / 4 * 6,GL_UNSIGNED_INT,MemoryUtil.NULL);

        vaoReady = true;
        if (clearOnDraw){
            this.reset();
        }
        for (int i = 0; i < this.format.elements.size();i++){
            glDisableVertexAttribArray(i);
        }

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
        glBindVertexArray(0);
    }

    public void drawLines(boolean clearOnDraw){
        if (vertexCount % 2 != 0){
            throw new RuntimeException("Incomplete buffer!");
        }
        glBindVertexArray(vao);
        this.loadVBO(clearOnDraw);
        this.loadLinesEBO(clearOnDraw);
        this.initiateVAO(clearOnDraw);

        glDrawElements(GL_LINES,this.vertexCount,GL_UNSIGNED_INT,MemoryUtil.NULL);

        vaoReady = true;
        if (clearOnDraw){
            this.reset();
        }
        for (int i = 0; i < this.format.elements.size();i++){
            glDisableVertexAttribArray(i);
        }

        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,0);
        glBindVertexArray(0);
    }

    public void initiateVAO(boolean clearOnDraw){

        int offset = 0;
        for (int i = 0; i < this.format.elements.size();i++){
            VertexFormat.Element element = this.format.getElement(i);

            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i,element.elementCount,element.glType,false,this.format.byteSize,offset);

            offset += element.fullByteSize;
        }
    }


    public void loadVBO(boolean clearOnDraw){
        glBindBuffer(GL_ARRAY_BUFFER,this.vbo);
        if (clearOnDraw || !vaoReady){
            buffer.flip();
            glBufferData(GL_ARRAY_BUFFER,buffer,GL_STATIC_DRAW);
            buffer.clear();
        }
    }

    public void loadEBO(boolean clearOnDraw){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,this.ebo);
        if (clearOnDraw || !vaoReady){
            IntBuffer buf = MemoryUtil.memAllocInt(vertexCount / 4 * 6);
            for (int i = 0; i < this.vertexCount / 4;i++){
                buf.put(i * 4);
                buf.put(1 + i * 4);
                buf.put(2 + i * 4);

                buf.put(2 + i * 4);
                buf.put(3 + i * 4);
                buf.put(i * 4);
            }
            buf.flip();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,buf,GL_STATIC_DRAW);
            MemoryUtil.memFree(buf);
        }
    }

    public void loadLinesEBO(boolean clearOnDraw){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,this.ebo);
        if (clearOnDraw || !vaoReady){
            IntBuffer buf = MemoryUtil.memAllocInt(vertexCount);
            for (int i = 0; i < this.vertexCount;i++){
                buf.put(i);
            }
            buf.flip();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,buf,GL_STATIC_DRAW);
            MemoryUtil.memFree(buf);
        }
    }



    public void putFloat(float a){
        this.ensureCapacity(Float.BYTES);
        this.buffer.putFloat(a);
    }

    public VertexBuffer position(Matrix4f m, Vector3f v){
        return this.position(m,v.x,v.y,v.z);
    }

    public VertexBuffer position(Vector3f v){
        return this.position(v.x,v.y,v.z);
    }

    public VertexBuffer position(float x,float y,float z){
        this.nextElement(VertexFormat.Element.POSITION);
        this.ensureCapacity(VertexFormat.Element.POSITION.fullByteSize);
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        return this;
    }

    public VertexBuffer position(Matrix4f transform,float x, float y, float z){
        this.nextElement(VertexFormat.Element.POSITION);
        this.ensureCapacity(VertexFormat.Element.POSITION.fullByteSize);
        Vector4f v = transform.transform(x,y,z,1,new Vector4f());
        this.putFloat(v.x);
        this.putFloat(v.y);
        this.putFloat(v.z);
        return this;
    }

    public VertexBuffer normal(float x, float y, float z){
        this.nextElement(VertexFormat.Element.NORMAL);
        this.ensureCapacity(VertexFormat.Element.NORMAL.fullByteSize);
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        return this;
    }

    public VertexBuffer color(float r,float g,float b,float a){
        this.nextElement(VertexFormat.Element.COLOR);
        this.ensureCapacity(VertexFormat.Element.COLOR.fullByteSize);
        this.putFloat(r);
        this.putFloat(g);
        this.putFloat(b);
        this.putFloat(a);
        return this;
    }

    public VertexBuffer uv(float u,float v){
        this.nextElement(VertexFormat.Element.UV);
        this.ensureCapacity(VertexFormat.Element.UV.fullByteSize);
        this.putFloat(u);
        this.putFloat(v);
        return this;
    }


    public void nextElement(VertexFormat.Element element){
        VertexFormat.Element e = this.format.getElement(currentElement);
        if (e != element){
            throw new RuntimeException("Wrong element lol " + element + " current is " + e);
        }
        currentElement = (currentElement + 1) % this.format.elements.size();
        if (currentElement == 0){
            vertexCount++;
        }
    }


    public void ensureCapacity(int bytes){
        int pos = buffer.position();
        int capacity = buffer.capacity();
        int finalCapacity = capacity;
        while (pos + bytes > finalCapacity){
            finalCapacity *= 2;
        }
        if (capacity != finalCapacity){
            buffer = MemoryUtil.memRealloc(buffer,finalCapacity);
        }
        buffer.limit(buffer.capacity());
    }


    public void reset(){
        this.buffer.clear();
        this.vertexCount = 0;
        this.vaoReady = false;
        this.currentElement = 0;
    }

    public void destroy(){
        MemoryUtil.memFree(buffer);
        this.buffer = null;
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }

    @Override
    public void close() {
        this.destroy();
    }
}
