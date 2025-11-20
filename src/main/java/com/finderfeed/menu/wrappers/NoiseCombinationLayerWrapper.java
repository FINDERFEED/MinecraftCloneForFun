package com.finderfeed.menu.wrappers;

import com.finderfeed.GlobalWorldParameters;
import com.finderfeed.Main;
import com.finderfeed.engine.textures.Texture;
import com.finderfeed.engine.textures.Texture2DSettings;
import com.finderfeed.menu.MainMenu;
import com.finderfeed.menu.NoiseLayerRedactorMenu;
import com.finderfeed.menu.wrappers.layer_combiner_wrappers.LayerCombinerWrapper;
import com.finderfeed.menu.wrappers.layer_combiner_wrappers.LayerCombinerWrapperRegistry;
import com.finderfeed.menu.wrappers.layer_combiner_wrappers.LayerCombinerWrapperType;
import com.finderfeed.noise_combiner.NoiseCombinationLayer;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.NoiseValueCombinerRegistry;
import com.finderfeed.util.Util;
import com.finderfeed.world.chunk.Chunk;
import imgui.ImGui;
import imgui.ImVec2;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.function.Function;

public class NoiseCombinationLayerWrapper extends ObjectWrapper<NoiseCombinationLayer> {

    private BufferedImage noiseImage;
    private Texture noiseTexture;

    private NoiseLayerRedactorMenu layerRedactorMenu;

    private LayerCombinerWrapper<?,?> layerCombinerWrapper;

    public NoiseCombinationLayerWrapper(NoiseCombinationLayer object) {
        super(object);
    }

    @Override
    public void renderWrappedObject() {
        ImGui.image(noiseTexture.getTextureId(), new ImVec2(100,100));
        ImGui.sameLine();

        if (ImGui.button("Edit noise layer")){
            if (layerRedactorMenu == null) {
                int id = MainMenu.takeNextFreeMenuId();
                layerRedactorMenu = new NoiseLayerRedactorMenu("Noise layer editor##" + id, this.getObject().getNoiseLayer());
                layerRedactorMenu.addOnChangeListener(() -> {
                    this.noiseChanged();
                    this.changeListener.run();
                });
                layerRedactorMenu.addOnCloseListener(()->{
                    this.layerRedactorMenu = null;
                });
                Main.window.getMainMenu().openMenu(layerRedactorMenu);
            }
        }

        this.renderLayerCombinerWrapperCombo();
        this.layerCombinerWrapper.renderWrappedObject();


    }

    @Override
    public void initialize() {
        super.initialize();
        this.initNoiseTexture(this.getObject().getNoiseLayer());
        this.initLayerCombinerWrapper(this.getObject().getCombiner());
        GlobalWorldParameters.addGlobalParameterChangeListener(this,this::noiseChanged);
    }

    @Override
    public void close() {
        super.close();
        if (layerRedactorMenu != null){
            layerRedactorMenu.setClosed(true);
        }
        layerCombinerWrapper.close();
        this.noiseImage.flush();
        this.noiseTexture.destroyTexture();
        GlobalWorldParameters.removeListener(this);
    }

    private void renderLayerCombinerWrapperCombo(){

        String currentWrapperType = this.getObject().getCombiner().getType().getRegistryId();

        if (ImGui.beginCombo("layerCombiner", currentWrapperType)){

            for (var layerCombiner : NoiseValueCombinerRegistry.NOISE_VALUE_COMBINERS.getObjectTypes()){

                var regId = layerCombiner.getRegistryId();

                if (ImGui.selectable(regId)){
                    var combiner = layerCombiner.generateObject();
                    this.getObject().setCombiner(combiner);
                    this.initLayerCombinerWrapper(combiner);
                    this.changeListener.run();
                    break;
                }

            }

            ImGui.endCombo();
        }

    }

    private void initLayerCombinerWrapper(FDNoiseValueCombiner<?> noiseValueCombiner){

        if (this.layerCombinerWrapper != null){
            this.layerCombinerWrapper.close();
            this.layerCombinerWrapper = null;
        }


        var type = noiseValueCombiner.getType();

        for (var e : LayerCombinerWrapperRegistry.LAYER_COMBINER_WRAPPERS.getObjectTypes()){

            var valueCombinerType = e.getValueCombinerType();
            if (valueCombinerType == type){
                LayerCombinerWrapper<?,?> wrapper = this.useFactory(e, noiseValueCombiner);
                this.layerCombinerWrapper = wrapper;
                break;
            }
        }

        if (this.layerCombinerWrapper == null) throw new RuntimeException("Layer combiner wrapper for type: " + noiseValueCombiner.getType() + " is not registered.");

        this.layerCombinerWrapper.initialize();
        this.layerCombinerWrapper.setChangeListener(()->{
            this.changeListener.run();
        });

    }

    private <D extends LayerCombinerWrapper<D, T>, T extends FDNoiseValueCombiner<T>> D useFactory(LayerCombinerWrapperType<D, T> wrapperType, FDNoiseValueCombiner<?> noiseValueCombiner){
        return wrapperType.getFactory().apply((T) noiseValueCombiner);
    }

    private void noiseChanged(){
        NoiseLayerRedactorMenu.paintNoise(this.getObject().getNoiseLayer(), this.noiseImage, this.noiseImage.getWidth());
        this.noiseTexture.updateTextureWithBufferedImage(this.noiseImage);
    }

    private void initNoiseTexture(NoiseLayer noiseLayer){
        if (noiseImage != null){
            noiseImage.flush();
            noiseImage = null;
        }

        if (noiseTexture != null){
            noiseTexture.destroyTexture();
            noiseTexture = null;
        }


        int texDimension = 64;
        BufferedImage bufferedImage = new BufferedImage(texDimension,texDimension,BufferedImage.TYPE_INT_ARGB);

        NoiseLayerRedactorMenu.paintNoise(noiseLayer, bufferedImage, bufferedImage.getWidth());

        ByteBuffer buffer = Util.bufferedImageToBuffer(bufferedImage);

        this.noiseTexture = new Texture("generated_noise", buffer, new Texture2DSettings()
                .width(texDimension)
                .height(texDimension)
        );

        this.noiseImage = bufferedImage;

        MemoryUtil.memFree(buffer);

    }

}
