package textualmold9830;

import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterLevelSceneServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
//This may be used in the future to allow multiple saves
public class GameManager {
    public static boolean hasGame(){
        return Files.exists(Path.of(Dungeon.GAME_FILE));
    }

    public static boolean  loadGame(){
        try {
            Dungeon.init();
            InterLevelSceneServer.restore();
            System.out.println("game loaded");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void startNewGame(){
        Dungeon.deleteGame(true);
        Dungeon.init();
        try {
            Files.createDirectories(Path.of("save/heroes/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InterLevelSceneServer.descend(null);
    }
    //This could be used in the future
    public static interface IGameManager /*I hate this name, I couldn't come up with a better one*/ {
        void loadGame();
        void startGame();
        boolean hasSave();
        void deleteGame();
    }
}
