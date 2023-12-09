package textualmold9830.plugins;


import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextureManager {
    private static int startCount = 128;
    private static HashMap<String, Texture> textures = new HashMap<>();
    private static HashMap<Integer, Texture> texturesByID = new HashMap<>();
    public static void putTexture(String textureName, BufferedImage image){
        if (!textures.containsKey(textureName)) {
            Texture tex = new Texture(startCount, image);
            textures.put(textureName,tex);
            texturesByID.put(tex.id, tex);
            startCount++;
        }else {
            int id = textures.get(textureName).id;
            Texture tex = new Texture(id, image);
            textures.put(textureName, tex);
            texturesByID.put(id, tex);
        }
    }
    public static Texture getTextureByID(int id){
     return texturesByID.get(id);
    }
    public static Texture getTextureByName(String name){
        return textures.get(name);
    }

}
