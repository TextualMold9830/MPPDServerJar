package textualmold9830;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.text.Font;
import javafx.stage.StageBuilder;
import javafx.stage.WindowEvent;

public class Main {
    public static void main(String[] args) {
        (new Runnable() {
            @Override
            public void run() {
                new JFXPanel(); // this will prepare JavaFX toolkit and environment
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        Server.startServer();
                        Label l = new Label("Hello world!");
                        Dungeon.init();
                        InterLevelSceneServer.descend(null);

                    }
                });
            }
        }).run();

    }
}
