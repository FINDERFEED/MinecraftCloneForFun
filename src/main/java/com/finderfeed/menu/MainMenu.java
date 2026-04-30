package com.finderfeed.menu;

import com.finderfeed.GlobalWorldParameters;
import com.finderfeed.Main;
import com.finderfeed.noise_combiner.NoiseCombination;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.google.gson.JsonObject;
import imgui.ImGui;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainMenu {

    public static final String FILE_EXTENSION = ".gtf";

    private static int MENU_ID = 0;

    private List<Menu> openedMenus = new ArrayList<>();
    private List<Runnable> delayedAfterMenuActions = new ArrayList<>();

    public MainMenu(){

    }

    public void renderGui(){
        if (ImGui.beginMainMenuBar()){
            this.renderMainMenuButtons();
            this.renderMenus();
            ImGui.endMainMenuBar();
        }
    }

    private void renderMainMenuButtons(){
        if (ImGui.button("Noise combiner menu") && openedMenus.stream().noneMatch(menu -> menu instanceof NoiseCombinerMenu)){
            NoiseCombination noiseCombination = GlobalWorldParameters.getCurrentNoiseCombination();
            this.openMenu(new NoiseCombinerMenu("Noise combiner",noiseCombination));
        }


        if (ImGui.button("Save project")){
            this.save();
        }

        if (ImGui.button("Load project")){
            this.load();
        }

        if (ImGui.button("Export model")){
            this.openMenu(new ModelExportMenu());
        }

        if (ImGui.button("Documentation")){

        }

    }

    private void load(){


        CompletableFuture.runAsync(()->{

            String filter = "*" + FILE_EXTENSION;

            MemoryStack memoryStack = MemoryStack.stackPush();

            PointerBuffer filters = memoryStack.mallocPointer(1);
            filters.put(memoryStack.UTF8(filter));
            filters.flip();


            String path = TinyFileDialogs.tinyfd_openFileDialog(
                    "Load project",
                    "project" + FILE_EXTENSION,
                    filters,
                    "Generated Terrain Files",
                    false
            );

            if (path != null && path.endsWith(FILE_EXTENSION)) {
                try{

                    Path p = Path.of(path);
                    var noiseCombination = GlobalWorldParameters.getCurrentNoiseCombination();
                    var gson = Main.GSON;

                    try (BufferedReader reader = Files.newBufferedReader(p)){
                        JsonObject object = gson.fromJson(reader, JsonObject.class);
                        noiseCombination.deserializeFromJson(object);
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            memoryStack.pop();

        });






    }



    private void save(){

        CompletableFuture.runAsync(()->{

            String filter = "*" + FILE_EXTENSION;

            MemoryStack memoryStack = MemoryStack.stackPush();

            PointerBuffer filters = memoryStack.mallocPointer(1);
            filters.put(memoryStack.UTF8(filter));
            filters.flip();


            String path = TinyFileDialogs.tinyfd_saveFileDialog(
                    "Save project",
                    "project" + FILE_EXTENSION,
                    filters,
                    "Generated Terrain Files"
            );

            if (path != null && path.endsWith(FILE_EXTENSION)) {
                try{

                    Path p = Path.of(path);

                    var noiseCombination = GlobalWorldParameters.getCurrentNoiseCombination();
                    var gson = Main.GSON;
                    try (BufferedWriter writer = Files.newBufferedWriter(p)) {
                        JsonObject jsonObject = new JsonObject();
                        noiseCombination.serializeToJson(jsonObject);
                        gson.toJson(jsonObject, writer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            memoryStack.pop();

        });
    }

    private void renderMenus(){
        var iterator = openedMenus.iterator();
        while (iterator.hasNext()){

            var menu = iterator.next();
            menu.renderMenu();
            if (menu.shouldClose()){
                menu.onClose();
                iterator.remove();
            }

        }

        delayedAfterMenuActions.forEach(Runnable::run);
        delayedAfterMenuActions.clear();

    }

    public void openMenu(Menu menu){
        delayedAfterMenuActions.add(()->{
            menu.onOpen();
            this.openedMenus.add(menu);
        });
    }

    public static int takeNextFreeMenuId(){
        int ret = MENU_ID;
        MENU_ID++;
        return ret;
    }


}
