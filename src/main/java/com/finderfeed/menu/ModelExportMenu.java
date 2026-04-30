package com.finderfeed.menu;

import com.finderfeed.GlobalWorldParameters;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModelExportMenu extends Menu {

    public ImFloat exportRadius = new ImFloat(100);

    public ImInt levelOfDetail = new ImInt(20);

    public ModelExportMenu() {
        super("Model export menu", new ImVec2(300,200));
    }

    @Override
    public void renderMenuContents() {
        if (ImGui.inputFloat("Export Radius", exportRadius)){

            if (exportRadius.get() <= 0.1f){
                exportRadius.set(0.5f);
            }

        }

        if (ImGui.inputInt("Level Of Detail", levelOfDetail)){

            if (levelOfDetail.get() < 2){
                levelOfDetail.set(2);
            }

        }

        if (ImGui.button("Export")){
            this.exportModel();
        }

    }

    private void exportModel(){
        CompletableFuture.runAsync(()->{

            String modelExtension = ".obj";

            String filter = "*" + modelExtension;

            MemoryStack memoryStack = MemoryStack.stackPush();

            PointerBuffer filters = memoryStack.mallocPointer(1);
            filters.put(memoryStack.UTF8(filter));
            filters.flip();


            String path = TinyFileDialogs.tinyfd_saveFileDialog(
                    "Export model",
                    "model" + modelExtension,
                    filters,
                    "Wavefront OBJ"
            );

            if (path != null && path.endsWith(modelExtension)) {
                try{

                    Path p = Path.of(path);

                    var noiseCombination = GlobalWorldParameters.getCurrentNoiseCombination();
                    try (BufferedWriter writer = Files.newBufferedWriter(p)) {
                        writer.append("Jojo golden wind");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            memoryStack.pop();

        }).thenRun(()->{
            this.setClosed(true);
        });
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }
}
