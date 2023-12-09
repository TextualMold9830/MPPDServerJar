package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.levels.Level;
import textualmold9830.plugins.Event;

/***
 * Fired when a dungeon finishes creating a new level
 ***/
public class DungeonPostGenerateLevelEvent extends Event {
    public Level level;

    public DungeonPostGenerateLevelEvent(Level level) {
        this.level = level;
    }
}
