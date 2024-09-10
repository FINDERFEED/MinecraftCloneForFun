package org.example.textures.atlases;


import org.example.Texture2DSettings;
import org.example.textures.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AtlasTexture {

    private static final List<String> FORMATS = List.of(
            "png",
            "jpg"
    );
    public int wh;
    public Texture atlas;
    public HashMap<String,TextureData> imageData = new HashMap<>();

    public AtlasTexture(String directory,int wh){
        this.wh = wh;
        BufferedImage atlasTexture = new BufferedImage(wh,wh,BufferedImage.TYPE_INT_ARGB);
        List<Image> images = getImagesFromDirectoryAndInner(directory);
        this.generate(atlasTexture,images);
        try {
            File f = new File("C:\\Users\\User\\Desktop\\MISC\\files\\image.png");
            f.createNewFile();
            ImageIO.write(atlasTexture, "png", f);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        ByteBuffer buffer = this.bufferedImageToBuffer(atlasTexture);
        Texture texture = new Texture(
                "generated_atlas",
                buffer,
                new Texture2DSettings()
                        .width(wh)
                        .height(wh)
        );
        this.atlas = texture;
        atlasTexture.flush();
        MemoryUtil.memFree(buffer);
    }

    //type int argb
    private ByteBuffer bufferedImageToBuffer(BufferedImage image){
        DataBufferInt dataBufferInt = (DataBufferInt) image.getRaster().getDataBuffer();
        int[] data = dataBufferInt.getData();
        ByteBuffer buffer = MemoryUtil.memAlloc(Integer.BYTES * data.length);
        //0xaarrggbb
        for (int col : data){
            int r = (col & 0x00ff0000) >> 16;
            int g = (col & 0x0000ff00);
            int b = (col & 0x000000ff);
            int a = (col & 0xff000000);
            col = a + (b << 16) + g + r;
            buffer.putInt(
                    col
            );
        }
        buffer.flip();

        return buffer;
    }

    public void generate(BufferedImage atlasTexture,List<Image> images){
        sortImagesList(images);
        int h = 0;
        try {
            while (!images.isEmpty()) {
                Image img = images.get(0);
                int currentMaxHeight = img.image().getHeight();
                Level level = new Level(0, wh, h, h + currentMaxHeight);

                fillLevel(atlasTexture,level, images);
                h += currentMaxHeight;
            }
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    private void fillLevel(BufferedImage atlasTexture,Level level,List<Image> images){
        List<Level> subLevels = new ArrayList<>();
        Iterator<Image> iter = images.iterator();
        while (iter.hasNext()){
            Image img = iter.next();
            if (level.height() < 0){
                throw new RuntimeException("Wtf level height is negative?");
            }
            if (doFit(level,img)){




                copyImageToImage(atlasTexture,img.image(),level.currentX,level.yStart);


                float u1 = (level.currentX /*+ 0.5f*/) / (float) atlasTexture.getWidth();
                float u2 = ((level.currentX /*- 0.5f*/) + img.image().getWidth()) / (float) atlasTexture.getWidth();
                float v2 = (level.yStart /*+ 0.5f*/) / (float) atlasTexture.getHeight();
                float v1 = ((level.yStart /*- 0.5f*/) + img.image().getHeight()) / (float) atlasTexture.getHeight();

                TextureData data = new TextureData(img.name(),img.image.getWidth(),img.image.getHeight(),u1,v1,u2,v2);
                this.imageData.put(img.name,data);




                Level sublevel = new Level(level.currentX,level.currentX + img.image().getWidth(),
                        level.yStart +img.image().getHeight(),level.yEnd);

                subLevels.add(sublevel);
                level.currentX += img.image().getWidth();
                img.image.flush();
                iter.remove();
            }else{
                continue;
            }
        }
        for (Level l : subLevels){
            fillLevel(atlasTexture,l,images);
        }
    }

    private boolean doFit(Level level,Image image){
        return level.height() >= image.image().getHeight() && image.image().getWidth() <= level.remainingWidth();
    }




    private static void copyImageToImage(BufferedImage main,BufferedImage copy,int xOffs,int yOffs){
        for (int x = 0; x < copy.getWidth(); x++){
            for (int y = 0; y < copy.getHeight(); y++){
                main.setRGB(x + xOffs,y + yOffs,copy.getRGB(x,y));
            }
        }
    }

    private static List<Image> getImagesFromDirectoryAndInner(String dir){

        URL url = AtlasTexture.class.getClassLoader().getResource("textures/block");


        try {
            File directory = new File(url.toURI());
            if (!directory.isDirectory()) {
                throw new RuntimeException(dir + " is not a directory!");
            }

            List<Image> images = new ArrayList<>();
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    images.addAll(getImagesFromDirectoryAndInner(file.getPath()));
                } else {
                    String name = file.getName();
                    String format = file.getName().split("\\.", 2)[1];
                    if (FORMATS.contains(format)) {
                        images.add(new Image(loadImage(file), name));
                    }
                }
            }
            return images;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void sortImagesList(List<Image> images){
        for (int i = 0; i < images.size() - 1; i++){
            for (int g = 0; g < images.size() - 1; g++){
                Image img1 = images.get(g);
                Image img2 = images.get(g + 1);
                BufferedImage i1 = img1.image;
                BufferedImage i2 = img2.image;
                if (i1.getHeight() < i2.getHeight()){
                    images.set(g + 1,img1);
                    images.set(g,img2);
                }
            }
        }
    }

    public static int diff(BufferedImage image){
        return Math.abs(image.getHeight() - image.getWidth());
    }

    private static BufferedImage loadImage(File file){
        try {
            return ImageIO.read(file);
        } catch (Exception e){
            throw new RuntimeException("Failed to load image: " + file,e);
        }
    }

    public static class Level{
        public int xStart;
        public int xEnd;
        public int yStart;
        public int yEnd;
        public int currentX;

        public Level(int xStart, int xEnd, int yStart, int yEnd) {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
            this.currentX = xStart;
        }

        public int remainingWidth(){
            return xEnd - currentX;
        }

        public int height(){
            return yEnd - yStart;
        }


    }

    public record Image(BufferedImage image, String name){
    }

    public record TextureData(String name,float width,float height,float u1,float v1,float u2,float v2){
    }
}
