package com.finderfeed.menu;

import com.finderfeed.Camera;
import com.finderfeed.GlobalWorldParameters;
import com.finderfeed.Main;
import com.finderfeed.entity.Entity;
import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseCombination;
import com.finderfeed.util.Pair;
import com.finderfeed.world.chunk.WorldChunk;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MathUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
                    String file = this.generateModelFile(noiseCombination);
                    try (BufferedWriter writer = Files.newBufferedWriter(p)) {
                        writer.append(file);
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

    private String generateModelFile(NoiseCombination noiseCombination){

        StringBuilder file = new StringBuilder("o Terrain\n");

        float exportRadius = this.exportRadius.get();
        float lod = this.levelOfDetail.get();

        float step = exportRadius / lod;

        List<Vector3f> positions = new ArrayList<>();
        List<Vector2f> vts = new ArrayList<>();

        Camera entity = Main.camera;
        Vector3d basePos = new Vector3d(entity.pos); basePos.y = WorldChunk.NOISE_Y;

        int seed = GlobalWorldParameters.getSeed();

        int wholeSteps = 0;

        for (float x = -exportRadius; x <= exportRadius; x+=step){
            wholeSteps++;
            float xt = (x + exportRadius) / (exportRadius * 2);
            for (float z = -exportRadius; z <= exportRadius; z+=step){

                float zt = (z + exportRadius) / (exportRadius * 2);

                Vector3d pos = basePos.add(x, 0, z, new Vector3d());

                pos.x /= GlobalWorldParameters.getCoordinateScale();
                pos.z /= GlobalWorldParameters.getCoordinateScale();

                ComputationContext computationContext = new ComputationContext(pos, seed);

                float point = Math.clamp(noiseCombination.compute(computationContext), 0, 1);

                var resultingPos = new Vector3f(x, point * WorldChunk.HEIGHT, z);
                positions.add(resultingPos);

                vts.add(new Vector2f(xt, zt));

            }
        }

        for (int i = 0; i < positions.size(); i++){
            Vector3f position = positions.get(i);
            file.append("v ").append(position.x).append(" ").append(position.y).append(" ").append(position.z).append("\n");
        }

        int normalsFileIndex = file.length() + 1;


        for (int i = 0; i < vts.size(); i++){
            Vector2f position = vts.get(i);
            file.append("vt ").append(position.x).append(" ").append(position.y).append("\n");
        }

        int normalId = 1;

        for (int x = 0; x < wholeSteps - 1; x++){
            for (int z = 0; z < wholeSteps - 1; z++){

                var p1 = this.getElementIn2D(positions, x, z, wholeSteps);
                var p2 = this.getElementIn2D(positions, x + 1, z, wholeSteps);
                var p3 = this.getElementIn2D(positions, x + 1, z + 1, wholeSteps);
                var p4 = this.getElementIn2D(positions, x, z + 1, wholeSteps);


                var vt1 = this.getElementIn2D(vts, x, z, wholeSteps);
                var vt2 = this.getElementIn2D(vts, x + 1, z, wholeSteps);
                var vt3 = this.getElementIn2D(vts, x + 1, z + 1, wholeSteps);
                var vt4 = this.getElementIn2D(vts, x, z + 1, wholeSteps);

                Vector3f normal = this.normal(p1.a,p2.a,p3.a);
                Vector3f normal2 = this.normal(p3.a,p4.a,p1.a);

                String normalString = "vn " + normal.x + " " + normal.y + " " + normal.z + "\n";
                String normalString2 = "vn " + normal2.x + " " + normal2.y + " " + normal2.z + "\n";
                file.insert(normalsFileIndex - 1, normalString);
                file.insert(normalsFileIndex - 1, normalString2);


                String firstPoint = (p1.b + 1) + "/" + (vt1.b + 1) + "/" + normalId;
                String secondPoint = (p2.b + 1) + "/" + (vt2.b + 1) + "/" + normalId;
                String thirdPoint = (p3.b + 1) + "/" + (vt3.b + 1) + "/" + normalId;

                file.append("f ").append(firstPoint).append(" ").append(secondPoint).append(" ").append(thirdPoint).append("\n");

                firstPoint = (p3.b + 1) + "/" + (vt3.b + 1) + "/" + (normalId + 1);
                secondPoint = (p4.b + 1) + "/" + (vt4.b + 1) + "/" + (normalId + 1);
                thirdPoint = (p1.b + 1) + "/" + (vt1.b + 1) + "/" + (normalId + 1);

                file.append("f ").append(firstPoint).append(" ").append(secondPoint).append(" ").append(thirdPoint).append("\n");

                normalId+=2;

            }
        }

        return file.toString();
    }

    private Vector3f normal(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f v1 = new Vector3f(p2).sub(p1);
        Vector3f v2 = new Vector3f(p3).sub(p1);

        Vector3f normal = v1.cross(v2);


        return normal.normalize();
    }

    private <T>  Pair<T, Integer> getElementIn2D(List<T> list, int x, int y, int length){
        int index = x + y * length;
        return new Pair<>(list.get(index), index);
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }
}
