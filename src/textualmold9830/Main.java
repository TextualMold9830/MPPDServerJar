package textualmold9830;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        initFolders();
        Preferences.load();
        Runtime.getRuntime().addShutdownHook(new Thread(Preferences::save));
        Server.startServer();
        Dungeon.init();
        InterLevelSceneServer.descend(null);
        Server.pluginManager.loadPlugins();
        initTextures();
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
                Server.textures.add(texture.getAbsolutePath());
                System.out.println("Added texture: "+ texture.getName().replace(".zip",""));
            }
        }
    }
}
