package textualmold9830;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.texturepack.TexturePackManager;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        initFolders();
        Preferences.load();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));
        startGame(args);
        initTextures();
        Server.pluginManager.loadPlugins();
        Server.startServer();
        System.out.println("Server started");

    }
    private static void initFolders(){
            new File("plugins").mkdirs();
            new File("config").mkdirs();
            new File("save/heroes/").mkdirs();
            new File("textures").mkdirs();
    }
    private static void initTextures(){
        File textureDir = new File("textures");
        for (File texture : textureDir.listFiles()) {
            if (texture.getName().endsWith(".zip")) {
                    TexturePackManager.addTexturePack(texture.toPath().toString());
                    System.out.println("Added texture: "+ texture.getName().replace(".zip",""));
            }
        }
    }
    private static void shutdown(){
        Preferences.save();
        Server.pluginManager.shutdownPlugins();
        Server.stopServer();
        try {
            Dungeon.saveAll();
            System.out.println("Saved the game in save folder");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void startGame(String[] args){
        if ((args.length > 0 && args[0] != null && args[0].equals("reset")) || !GameManager.hasGame()) {
            GameManager.startNewGame();
        } else {
            if(!GameManager.loadGame()){
                GameManager.startNewGame();
            };

        }
    }
}
