package textualmold9830;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import com.watabou.pixeldungeon.texturepack.TexturePackManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class Main {
    public static void main(String[] args) {
        initFolders();
        Preferences.load();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));
        Server.startServer();
        Dungeon.init();
        InterLevelSceneServer.descend(null);
        initTextures();
        Server.pluginManager.loadPlugins();
        System.out.println("Server started");

    }
    private static void initFolders(){
            new File("plugins").mkdirs();
            new File("config").mkdirs();
            new File("save").mkdirs();
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
    }
}
