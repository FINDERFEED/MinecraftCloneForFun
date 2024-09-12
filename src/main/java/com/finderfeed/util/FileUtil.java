package com.finderfeed.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class FileUtil {


    public static String readPrimitiveFileFromResources(String path){
        InputStream stream = FileUtil.class.getClassLoader().getResourceAsStream(path);
        Objects.requireNonNull(stream);

        InputStreamReader isreader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isreader);
        String s;
        String finalFile = "";
        try {
            while ((s = reader.readLine()) != null) {
                finalFile += s + "\n";
            }
            reader.close();
            return  finalFile;
        }catch (IOException e){
            throw new RuntimeException("Cannot load shader file: " + path);
        }
    }

}
