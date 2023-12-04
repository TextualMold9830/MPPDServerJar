package textualmold9830.plugins;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginUtils {
    public static Path getConfigDirectory(Plugin plugin){
        return Path.of("config/"+plugin.getName());
    }
    public static int getHeroIndex(Hero hero){
        for (int i = 0; i < Dungeon.heroes.length; i++) {
            if (Dungeon.heroes[i].equals(hero)){
                return i;
            }
        }
        return 0;
    }
    public static File getDefaultConfigFile(Plugin plugin){
        return Path.of("config/"+plugin.getName()+"/config.txt").toFile();
    }
    public static String getConfigData(Plugin plugin){
        try {
            return Files.readString(Path.of("config/"+plugin.getName()+"/config.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return plugin.defaultConfig();
    }
}
