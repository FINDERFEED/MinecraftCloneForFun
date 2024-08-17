package org.example;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class VertexFormat {

    public static VertexFormat POSITION = new VertexFormat().addElement(Element.POSITION);
    public static VertexFormat POSITION_COLOR = new VertexFormat().addElement(Element.POSITION).addElement(Element.COLOR);
    public static VertexFormat POSITION_COLOR_UV = new VertexFormat().addElement(Element.POSITION).addElement(Element.COLOR).addElement(Element.UV);
    public static VertexFormat POSITION_COLOR_UV_NORMAL = new VertexFormat().addElement(Element.POSITION).addElement(Element.COLOR).addElement(Element.UV).addElement(Element.NORMAL);
    public static VertexFormat POSITION_UV = new VertexFormat().addElement(Element.POSITION).addElement(Element.UV);


    public List<Element> elements = new ArrayList<>();
    public int byteSize;

    public VertexFormat(){}


    public VertexFormat addElement(Element element){
        this.byteSize += element.fullByteSize;
        this.elements.add(element);
        return this;
    }

    public Element getElement(int index){
        return elements.get(index);
    }



    public enum Element{
        POSITION("position",GL11.GL_FLOAT,Float.BYTES,3),
        COLOR("color",GL11.GL_FLOAT,Float.BYTES,4),
        UV("uv",GL11.GL_FLOAT,Float.BYTES,2),
        NORMAL("normal",GL11.GL_FLOAT,Float.BYTES,3),
        ;

        public String name;
        public int glType;
        public int elementByteSize;
        public int elementCount;
        public int fullByteSize;

        Element(String name,int glType,int elementByteSize,int elementCount){
            this.glType = glType;
            this.elementByteSize = elementByteSize;
            this.elementCount = elementCount;
            this.fullByteSize = elementByteSize * elementCount;
            this.name = name;
        }

    }

}
