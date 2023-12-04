package textualmold9830.plugins;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.hero.Hero;

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
}
