package com.finderfeed.engine.immediate_buffer_supplier;

import com.finderfeed.VertexBuffer;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;

import java.util.HashMap;

public class ImmediateBufferSupplier {

    private static HashMap<RenderOptions, VertexBuffer> buffers = new HashMap<>();

    private static Pair<RenderOptions,VertexBuffer> current = null;

    public static VertexBuffer get(RenderOptions options){
        VertexBuffer buffer = buffers.get(options);
        if (buffer == null){
            buffer = new VertexBuffer(2048,options.getFormat());
            buffers.put(options,buffer);
        }
        if (current != null && current.first() != options){
            drawCurrent();
        }
        current = new ObjectObjectImmutablePair<>(options,buffer);
        return buffer;
    }

    public static void drawCurrent(){
        RenderOptions currentOptions = current.first();
        VertexBuffer b = current.right();
        currentOptions.run();
        switch (currentOptions.getDrawMode()){
            case LINES -> {
                b.drawLines(true);
            }
            case QUADS -> {
                b.draw(true);
            }
        }
        currentOptions.clear();
        current = null;
    }


}
