package textualmold9830;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import javafx.application.Platform;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Preferences.load();
        Runtime.getRuntime().addShutdownHook(new Thread(Preferences::save));
        Platform.startup(()->{
            Toolkit.getDefaultToolkit().beep();
        });
        Server.startServer();
        Dungeon.init();
        InterLevelSceneServer.descend(null);

    }
}
