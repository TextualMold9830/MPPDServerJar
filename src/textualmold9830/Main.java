package textualmold9830;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        initFolders();
        Preferences.load();
        Runtime.getRuntime().addShutdownHook(new Thread(Preferences::save));
        Server.startServer();
        Dungeon.init();
        InterLevelSceneServer.descend(null);
        Server.pluginManager.loadPlugins();

    }
    private static void initFolders(){
        try {
            Files.createDirectory(Path.of("plugins"));
            Files.createDirectory(Path.of("config"));
        } catch (IOException e) {
        }
    }
}
