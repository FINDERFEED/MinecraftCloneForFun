package com.finderfeed.menu;

import com.finderfeed.menu.wrappers.NoiseCombinationLayerWrapper;
import com.finderfeed.noise_combiner.NoiseCombination;
import com.finderfeed.noise_combiner.NoiseCombinationLayer;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.HashMap;
import java.util.Iterator;

public class NoiseCombinerMenu extends Menu {

    private HashMap<NoiseCombinationLayer, NoiseCombinationLayerWrapper> layerToWrapper = new HashMap<>();

    private NoiseCombination noiseCombination;

    public NoiseCombinerMenu(String menuTitle, NoiseCombination noiseCombination) {
        super(menuTitle, new ImVec2(1000,1000));
        this.noiseCombination = noiseCombination;
    }

    @Override
    public void renderMenuContents() {

        if (ImGui.button("Add noise layer")){
            var combinationLayers = this.noiseCombination.getNoiseCombinationLayers();
            combinationLayers.add(new NoiseCombinationLayer());
        }

        this.renderNoiseLayers();

    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    private void layersChanged(){

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

        if (ImGui.button("Delete noise layer")){
            this.noiseCombination.getNoiseCombinationLayers().remove(layer);
        }

        var wrapper = layerToWrapper.get(layer);
        wrapper.renderWrappedObject();

        ImGui.popID();

    }


}
