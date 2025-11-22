package com.finderfeed.menu;

import com.finderfeed.GlobalWorldParameters;
import com.finderfeed.Main;
import com.finderfeed.engine.textures.Texture;
import com.finderfeed.engine.textures.Texture2DSettings;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperRegistry;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperType;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.value_modifier.instances.AddValueModifier;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.finderfeed.util.FDColor;
import com.finderfeed.util.Util;
import com.finderfeed.world.chunk.Chunk;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class NoiseLayerRedactorMenu extends Menu {

    private List<Runnable> onCloseListeners = new ArrayList<>();
    private List<Runnable> onChangeListeners = new ArrayList<>();

    private BufferedImage noiseImage;
    private Texture noiseTexture;

    private NoiseLayer noiseLayer;

    private NoiseWrapper<?,?> noiseWrapper;

    private String currentNoiseType;

    private final List<ValueModifierWrapper<?,?>> valueModifierWrappers = new ArrayList<>();

    public NoiseLayerRedactorMenu(String menuTitle, NoiseLayer noiseLayer) {
        super(menuTitle, new ImVec2(1000, 1000));
        this.noiseLayer = noiseLayer;
        this.currentNoiseType = noiseLayer.getNoise().getObjectType().getRegistryId();
        var noise = this.noiseLayer.getNoise();
        this.initNoiseWrapper(noise);
        this.initValueModifierWrappers(noiseLayer.getValueModifiers());
    }

    @Override
    public void renderMenuContents() {

        int noiseWindowSize = 350;

        ImGui.beginChild("noise", new ImVec2(noiseWindowSize,noiseWindowSize + 30));
        ImGui.image(noiseTexture.getTextureId(), new ImVec2(noiseWindowSize,noiseWindowSize));
        this.addNoiseSelectionCombo();
        ImGui.endChild();

        ImGui.sameLine();
        ImGui.beginChild("noiseParams", new ImVec2(0,330));
        ImGui.text("Noise parameters");
        noiseWrapper.renderWrappedObject();

        ImGui.endChild();

        ImGui.separator();

        this.addValueModifierUiElements(noiseWindowSize);

        ImGui.endChild();
    }

    private void addValueModifierUiElements(int noiseWindowSize){
        ImGui.beginChild("noiseModifiers");
        var valueModifiers = this.noiseLayer.getValueModifiers();


        ImGui.pushStyleColor(ImGuiCol.Button, 0,128,0,255);
        if (ImGui.button("Add noise modifier")){
            this.noiseLayer.getValueModifiers().add(new AddValueModifier());
            this.initValueModifierWrappers(valueModifiers);
        }
        ImGui.popStyleColor();
        Util.insertSimpleTooltip("Add a new modifier for this noise. Modifiers are processed from top to bottom.");


        ImGui.separator();

        for (int i = 0; i < valueModifiers.size(); i++){

            ImGui.pushID("valueModifier_" + i);

            ImGui.text("Modifer " + i);


            ImGui.pushStyleColor(ImGuiCol.Button, 0.5f,0f,0f,1f);
            if (ImGui.button("Remove modifier")){
                valueModifiers.remove(i);
                var valueMod = this.valueModifierWrappers.get(i);
                valueMod.close();
                this.initValueModifierWrappers(valueModifiers);
                this.noiseChanged();
                ImGui.popID();
                ImGui.popStyleColor();
                break;
            }
            ImGui.popStyleColor();

            ImGui.sameLine();
            if (ImGui.arrowButton("Move Up",ImGuiDir.Up)){
                if (noiseLayer.moveValueModifier(i, true)){
                    this.initValueModifierWrappers(valueModifiers);
                    this.noiseChanged();
                    ImGui.popID();
                    break;
                }
            }

            ImGui.sameLine();
            if (ImGui.arrowButton("Move Down",ImGuiDir.Down)){
                if (noiseLayer.moveValueModifier(i, false)){
                    this.initValueModifierWrappers(valueModifiers);
                    this.noiseChanged();
                    ImGui.popID();
                    break;
                }

            }


            ImGui.beginChild("valueModifier" + i,new ImVec2(0, 60));

            this.addValueModifierTypeCombo(i);
            var valueModifierWrapper = this.valueModifierWrappers.get(i);
            valueModifierWrapper.renderWrappedObject();

            ImGui.endChild();

            ImGui.separator();

            ImGui.popID();
        }



    }

    private void addNoiseSelectionCombo(){
        if (ImGui.beginCombo("Noise type", currentNoiseType)){

            for (var noise : NoiseRegistry.NOISE_REGISTRY.getObjectTypes()){
                var regId = noise.getRegistryId();
                if (ImGui.selectable(regId)){
                    FDNoise<?> selectedNoise = noise.generateObject();
                    noiseLayer.setNoise(selectedNoise);
                    this.initNoiseWrapper(selectedNoise);
                    currentNoiseType = regId;
                    this.noiseChanged();
                    break;
                }

            }

            ImGui.endCombo();
        }

    }


    private void addValueModifierTypeCombo(int valueModifierId){

        var valueModifiers = this.noiseLayer.getValueModifiers();
        FDValueModifier<?> fdValueModifier = valueModifiers.get(valueModifierId);

        var objectType = fdValueModifier.getObjectType();

        if (ImGui.beginCombo("Value modifier type", objectType.getRegistryId())){

            for (var modifier : NoiseValueModifierRegistry.VALUE_MODIFIERS.getObjectTypes()){
                var regId = modifier.getRegistryId();
                if (ImGui.selectable(regId)){

                    FDValueModifier<?> newValueModifier = modifier.generateObject();
                    valueModifiers.set(valueModifierId, newValueModifier);

                    this.initValueModifierWrappers(valueModifiers);

                    this.noiseChanged();
                    break;
                }

            }

            ImGui.endCombo();
        }

    }




    @Override
    public void onOpen() {
        this.initNoiseTexture(this.noiseLayer);
        GlobalWorldParameters.addGlobalParameterChangeListener(this, this::noiseChanged);
    }

    @Override
    public void onClose() {
        GlobalWorldParameters.removeListener(this);
        this.onCloseListeners.forEach(Runnable::run);
        noiseImage.flush();
        noiseTexture.destroyTexture();
        noiseWrapper.close();
    }

    public void addOnCloseListener(Runnable runnable){
        this.onCloseListeners.add(runnable);
    }

    private void noiseChanged(){
        paintNoise(this.noiseLayer, this.noiseImage, this.noiseImage.getWidth());
        this.noiseTexture.updateTextureWithBufferedImage(this.noiseImage);
        this.triggerOnChangeListeners();
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

        paintNoise(noiseLayer, bufferedImage, bufferedImage.getWidth());

        ByteBuffer buffer = Util.bufferedImageToBuffer(bufferedImage);

        this.noiseTexture = new Texture("generated_noise", buffer, new Texture2DSettings()
                .width(texDimension)
                .height(texDimension)
        );

        this.noiseImage = bufferedImage;

        MemoryUtil.memFree(buffer);
    }

    public static void paintNoise(NoiseLayer noiseLayer, BufferedImage bufferedImage, int blockDiameter){

        var pos = Main.camera.pos;
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
                float value = noiseLayer.computeValue(computationContext1);

                value = Math.clamp(value, 0,1);

                int color = new FDColor(value,value,value,1f).encode();

                bufferedImage.setRGB(x,z, color);

            }
        }

    }

    private void initValueModifierWrappers(List<FDValueModifier<?>> fdValueModifiers){

        this.valueModifierWrappers.clear();

        for (var valueModifier : fdValueModifiers){

            ValueModifierWrapper<?,?> valueModifierWrapper = null;

            var objectType = valueModifier.getObjectType();
            for (var wrapperType : ValueModifierWrapperRegistry.VALUE_MODIFIER_WRAPPERS.getObjectTypes()){
                if (objectType == wrapperType.getValueModifierType()){
                    valueModifierWrapper = this.useValueModifierWrapperType(wrapperType, valueModifier);
                    valueModifierWrapper.setChangeListener(this::noiseChanged);
                    break;
                }
            }

            if (valueModifierWrapper == null) throw new RuntimeException("No wrapper registered for value modifier type: " + objectType);

            valueModifierWrapper.initialize();
            this.valueModifierWrappers.add(valueModifierWrapper);

        }

    }

    private void initNoiseWrapper(FDNoise<?> fdNoise){
        var type = fdNoise.getObjectType();

        NoiseWrapper<?,?> noiseWrapper = null;

        for (var wrapperType : NoiseWrapperRegistry.NOISE_WRAPPERS.getObjectTypes()){
            if (type == wrapperType.getNoiseObjectType()){
                noiseWrapper = this.useNoiseWrapperType(wrapperType, fdNoise);
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

    private void triggerOnChangeListeners(){
        onChangeListeners.forEach(Runnable::run);
    }

    public void addOnChangeListener(Runnable runnable){
        this.onChangeListeners.add(runnable);
    }


    private <T extends NoiseWrapper<T, D>, D extends FDNoise<D>> NoiseWrapper<T, D> useNoiseWrapperType(NoiseWrapperType<T, D> noiseWrapperType, FDNoise<?> fdNoise){
        return noiseWrapperType.generateWrapper((D) fdNoise);
    }

    private <T extends ValueModifierWrapper<T, D>, D extends FDValueModifier<D>> ValueModifierWrapper<T, D> useValueModifierWrapperType(ValueModifierWrapperType<T, D> noiseWrapperType, FDValueModifier<?> valueModifier){
        return noiseWrapperType.getWrapperFactory().apply((D) valueModifier);
    }

}
