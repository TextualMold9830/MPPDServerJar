package textualmold9830;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import javafx.application.Platform;
import textualmold9830.plugins.PluginManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static PluginManager pluginManager = new PluginManager();
    public static void main(String[] args) {
        initFolders();
        Platform.startup(()->{

        });
        Server.startServer();
        Dungeon.init();
        InterLevelSceneServer.descend(null);
        pluginManager.loadPlugins();

    }
    private static void initFolders(){
        try {
            Files.createDirectory(Path.of("plugins"));
        } catch (IOException e) {
        }
    }
}
