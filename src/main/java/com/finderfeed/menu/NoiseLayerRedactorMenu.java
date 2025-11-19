package com.finderfeed.menu;

import com.finderfeed.Main;
import com.finderfeed.engine.textures.Texture;
import com.finderfeed.engine.textures.Texture2DSettings;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.FDNoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.FDNoiseWrapperRegistry;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperType;
import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.util.FDColor;
import com.finderfeed.util.MathUtil;
import com.finderfeed.util.Util;
import com.finderfeed.world.chunk.Chunk;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class NoiseLayerRedactorMenu extends Menu {

    private BufferedImage noiseImage;
    private Texture noiseTexture;

    private NoiseLayer noiseLayer;

    private FDNoiseWrapper<?,?> noiseWrapper;

    public NoiseLayerRedactorMenu(String menuTitle, NoiseLayer noiseLayer) {
        super(menuTitle, new ImVec2(400, 600));
        this.noiseLayer = noiseLayer;
        var noise = this.noiseLayer.getNoise();
        this.initNoiseWrapper(noise);
    }

    @Override
    public void renderMenuContents() {

        ImGui.image(noiseTexture.getTextureId(), new ImVec2(300,300));
        ImGui.sameLine();

        ImGui.beginChild("noiseParams");
        ImGui.text("Noise parameters");
        noiseWrapper.renderWrappedObject();

        ImGui.endChild();


    }

    @Override
    public void onOpen() {

        this.initNoiseTexture(this.noiseLayer);

    }

    @Override
    public void onClose() {
        noiseImage.flush();
        noiseTexture.destroyTexture();
        noiseWrapper.close();
    }

    private void noiseChanged(){
        paintNoise(this.noiseLayer, this.noiseImage);
        this.noiseTexture.updateTextureWithBufferedImage(this.noiseImage);
    }

    private void initNoiseTexture(NoiseLayer noiseLayer){

        if (noiseImage != null){
            noiseImage.flush();
        }
        if (noiseTexture != null){
            noiseTexture.destroyTexture();
        }

        int renderDistance = Main.chunkRenderDistance;
        int texDimension = renderDistance * Chunk.CHUNK_SIZE * 2;
        BufferedImage bufferedImage = new BufferedImage(texDimension,texDimension,BufferedImage.TYPE_INT_ARGB);

        paintNoise(noiseLayer, bufferedImage);

        ByteBuffer buffer = Util.bufferedImageToBuffer(bufferedImage);

        this.noiseTexture = new Texture("generated_noise", buffer, new Texture2DSettings()
                .width(texDimension)
                .height(texDimension)
        );

        this.noiseImage = bufferedImage;

        MemoryUtil.memFree(buffer);
    }

    public static void paintNoise(NoiseLayer noiseLayer, BufferedImage bufferedImage){

        var pos = new Vector3i(Main.mainEntity.getBlockPos());
        int seed = Main.seed;
        double coordinateScale = Main.coordinateScale;
        if (coordinateScale <= 0){
            coordinateScale = 1;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x = 0; x < width; x++){

            double xCoord = (pos.x - width / 2d + x) / coordinateScale;

            for (int z = 0; z < height; z++){

                double zCoord = (pos.z - height / 2d + z) / coordinateScale;

                Vector3d computePos = new Vector3d(xCoord, 32.545, zCoord);
                ComputationContext computationContext1 = new ComputationContext(computePos, seed);
                float value = noiseLayer.computeValue(computationContext1);

                value = Math.clamp(value, -1,1);
                value = (value + 1) / 2f;

                int color = new FDColor(value,value,value,1f).encode();

                bufferedImage.setRGB(x,z, color);

            }
        }



    }

    private void initNoiseWrapper(FDNoise<?> fdNoise){
        var type = fdNoise.getObjectType();

        FDNoiseWrapper<?,?> noiseWrapper = null;

        for (var wrapperType : FDNoiseWrapperRegistry.NOISE_WRAPPERS.getObjectTypes()){
            if (type == wrapperType.getNoiseObjectType()){
                noiseWrapper = this.useWrapperType(wrapperType, fdNoise);
                break;
            }
        }

        if (noiseWrapper == null) throw new RuntimeException("No wrapper is registered for noise type: " + type);

        if (this.noiseWrapper != null){
            this.noiseWrapper.close();
        }

        this.noiseWrapper = noiseWrapper;
        noiseWrapper.initialize();
        this.noiseWrapper.setChangeListener(this::noiseChanged);

    }



    private <T extends FDNoiseWrapper<T, D>, D extends FDNoise<D>> FDNoiseWrapper<T, D> useWrapperType(NoiseWrapperType<T, D> noiseWrapperType, FDNoise<?> fdNoise){
        return noiseWrapperType.generateWrapper((D) fdNoise);
    }

}
