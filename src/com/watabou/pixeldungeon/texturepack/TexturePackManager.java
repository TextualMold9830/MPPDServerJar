package com.watabou.pixeldungeon.texturepack;

import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.sprites.CharSprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TexturePackManager {
    public static HashMap<String, String> animationMap = new HashMap<>();

    public static void addTexturePack(String path){
        try {
            ZipFile zip = new ZipFile(path);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(!entry.isDirectory()){
                    if (entry.getName().contains("animations") && entry.getName().endsWith(".json")){
                        String name = entry.getName();
                        while (name.contains("/")){
                            name = name.substring(name.indexOf('/')+1);
                        }
                        animationMap.put(name.replace(".json",""), name);
                        System.out.println("added animation with name: " + name);
                    }
                }
            }
            zip.close();
            Server.textures.add(Base64.getEncoder().encodeToString(Files.readAllBytes(Path.of(path))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Nullable
    public static String getMobAnimation(@NotNull Class<? extends CharSprite> mobClass){
        System.out.println("Caller: " + mobClass.getSimpleName());
        return animationMap.get(mobClass.getSimpleName().replace("Sprite","").toLowerCase(Locale.ROOT));
    }
}
