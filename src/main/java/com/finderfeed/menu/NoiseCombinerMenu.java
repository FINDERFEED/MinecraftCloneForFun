package com.finderfeed.menu;

import com.finderfeed.GlobalWorldParameters;
import com.finderfeed.Main;
import com.finderfeed.engine.textures.Texture;
import com.finderfeed.engine.textures.Texture2DSettings;
import com.finderfeed.menu.wrappers.NoiseCombinationLayerWrapper;
import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseCombination;
import com.finderfeed.noise_combiner.NoiseCombinationLayer;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.util.FDColor;
import com.finderfeed.util.Util;
import com.finderfeed.world.chunk.Chunk;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

public class NoiseCombinerMenu extends Menu {

    private HashMap<NoiseCombinationLayer, NoiseCombinationLayerWrapper> layerToWrapper = new HashMap<>();

    private NoiseCombination noiseCombination;

    private Texture noiseTexture;
    private BufferedImage noiseImage;

    private final ImInt seed;
    private final ImFloat coordinateScale;
    private final float[] noiseScale;

    public NoiseCombinerMenu(String menuTitle, NoiseCombination noiseCombination) {
        super(menuTitle, new ImVec2(1000,1000));
        this.noiseCombination = noiseCombination;
        seed = new ImInt(GlobalWorldParameters.getSeed());
        coordinateScale = new ImFloat((float) GlobalWorldParameters.getCoordinateScale());
        noiseScale = new float[]{(float) GlobalWorldParameters.getNoiseScale()};
    }

    @Override
    public void renderMenuContents() {

        int noiseImageDimension = 500;
        ImGui.beginChild("resultingNoise", new ImVec2(noiseImageDimension,0));

        ImGui.image(noiseTexture.getTextureId(),new ImVec2(noiseImageDimension,noiseImageDimension));

        if (ImGui.inputInt("Seed", seed)){
            GlobalWorldParameters.setSeed(seed.get());
        }
        Util.insertSimpleTooltip("The global world seed");

        if (ImGui.inputFloat("Coordinate scale", coordinateScale)){
            GlobalWorldParameters.setCoordinateScale(coordinateScale.get());
        }
        Util.insertSimpleTooltip("""
                Global world coordinate scale.
                
                This parameter controls how the world is squished.
                
                The less the value of this parameter the greater world will squish.
                """);

        if (ImGui.sliderFloat("Zoom", noiseScale,0.01f, 10f)){
            GlobalWorldParameters.setNoiseScale(noiseScale[0]);
        }
        Util.insertSimpleTooltip("""
                Zoom noises.
                
                Does not affect world generation, purely visual change.
                """);


        ImGui.endChild();

        ImGui.sameLine();
        ImGui.beginChild("noiseLayers");

        ImGui.pushStyleColor(ImGuiCol.Button, 0,128,0,255);
        if (ImGui.button("Add noise layer")){
            var combinationLayers = this.noiseCombination.getNoiseCombinationLayers();
            combinationLayers.add(new NoiseCombinationLayer());
            this.layersChanged();
        }
        ImGui.popStyleColor();

        ImGui.separator();
        this.renderNoiseLayers();
        ImGui.endChild();
    }

    @Override
    public void onOpen() {
        this.initNoiseTexture(this.noiseCombination);
        GlobalWorldParameters.addGlobalParameterChangeListener(this, this::layersChanged);
    }

    @Override
    public void onClose() {
        for (var entry : layerToWrapper.entrySet()){
            var wrapper = entry.getValue();
            wrapper.close();
        }
        this.noiseImage.flush();
        this.noiseTexture.destroyTexture();
        GlobalWorldParameters.removeListener(this);
    }

    private void layersChanged(){
        paintNoise(this.noiseCombination, this.noiseImage, this.noiseImage.getWidth());
        noiseTexture.updateTextureWithBufferedImage(this.noiseImage);
    }

    private void initNoiseTexture(NoiseCombination noiseCombination){

        if (noiseImage != null){
            noiseImage.flush();
        }
        if (noiseTexture != null){
            noiseTexture.destroyTexture();
        }

        int renderDistance = Main.chunkRenderDistance;
        int texDimension = renderDistance * Chunk.CHUNK_SIZE * 2;
        BufferedImage bufferedImage = new BufferedImage(texDimension,texDimension,BufferedImage.TYPE_INT_ARGB);

        paintNoise(noiseCombination, bufferedImage, bufferedImage.getWidth());

        ByteBuffer buffer = Util.bufferedImageToBuffer(bufferedImage);

        this.noiseTexture = new Texture("generated_noise", buffer, new Texture2DSettings()
                .width(texDimension)
                .height(texDimension)
        );

        this.noiseImage = bufferedImage;

        MemoryUtil.memFree(buffer);
    }

    public static void paintNoise(NoiseCombination noiseLayer, BufferedImage bufferedImage, int blockDiameter){

        var pos = new Vector3d(Main.camera.pos);
        int seed = GlobalWorldParameters.getSeed();
        double coordinateScale = GlobalWorldParameters.getCoordinateScale();
        if (coordinateScale <= 0){
            coordinateScale = 1;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x = 0; x < width; x++){

            float xp = x / (float) (width - 1);

            double xCoordOffset = (-blockDiameter / 2d + blockDiameter * xp) * GlobalWorldParameters.getNoiseScale();

            double xCoord = (pos.x + xCoordOffset) / coordinateScale;

            for (int z = 0; z < height; z++){

                float zp = z / (float) (width - 1);

                double zCoordOffset = (-blockDiameter / 2d + blockDiameter * zp) * GlobalWorldParameters.getNoiseScale();

                double zCoord = (pos.z + zCoordOffset) / coordinateScale;

                Vector3d computePos = new Vector3d(xCoord, 131.04324, zCoord);
                ComputationContext computationContext1 = new ComputationContext(computePos, seed);
                float value = noiseLayer.compute(computationContext1);

                value = Math.clamp(value, 0,1);

                int color = new FDColor(value,value,value,1f).encode();

                bufferedImage.setRGB(x,z, color);

            }
        }

    }
    private void renderNoiseLayers(){

        var combinationLayers = this.noiseCombination.getNoiseCombinationLayers();

        for (int i = 0; i < combinationLayers.size(); i++){

            var noiseCombinationLayer = combinationLayers.get(i);

            if (!layerToWrapper.containsKey(noiseCombinationLayer)){
                var wrapper = this.wrapperForNoiseCombinationLayer(noiseCombinationLayer);
                layerToWrapper.put(noiseCombinationLayer, wrapper);
            }

            this.renderCombinationLayer(noiseCombinationLayer, i);
        }

        this.detectAndRemoveUnusedWrappers();

    }

    private void detectAndRemoveUnusedWrappers(){
        var iterator = this.layerToWrapper.entrySet().iterator();
        while (iterator.hasNext()){
            var entry = iterator.next();
            var wrapper = entry.getValue();
            var layer = wrapper.getObject();
            if (!this.noiseCombination.getNoiseCombinationLayers().contains(layer)){
                wrapper.close();
                iterator.remove();
            }
        }
    }


    private NoiseCombinationLayerWrapper wrapperForNoiseCombinationLayer(NoiseCombinationLayer noiseCombinationLayer){
        NoiseCombinationLayerWrapper wrapper = new NoiseCombinationLayerWrapper(noiseCombinationLayer);
        wrapper.initialize();
        wrapper.setChangeListener(this::layersChanged);
        return wrapper;
    }

    private void renderCombinationLayer(NoiseCombinationLayer layer, int index){

        ImGui.pushID("noiseCombinationLayer" + index);

        ImGui.pushStyleColor(ImGuiCol.Button, 128, 0, 0, 255);
        if (ImGui.button("Delete noise layer")){
            this.noiseCombination.getNoiseCombinationLayers().remove(layer);
            if (this.noiseCombination.getNoiseCombinationLayers().isEmpty()){
                this.noiseCombination.getNoiseCombinationLayers().add(new NoiseCombinationLayer());
            }
            this.layersChanged();
        }
        ImGui.popStyleColor();

        ImGui.sameLine();
        if (ImGui.arrowButton("moveNoiseCombinationUp", ImGuiDir.Up)){
            this.noiseCombination.moveCombinationLayer(index, true);
            this.layersChanged();
        }

        ImGui.sameLine();
        if (ImGui.arrowButton("moveNoiseCombinationDown", ImGuiDir.Down)){
            this.noiseCombination.moveCombinationLayer(index, false);
            this.layersChanged();
        }

        var wrapper = layerToWrapper.get(layer);
        wrapper.renderWrappedObject();

        ImGui.separator();

        ImGui.popID();

    }


}
