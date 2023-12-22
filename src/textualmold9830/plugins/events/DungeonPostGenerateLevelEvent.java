package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.levels.Level;
import textualmold9830.plugins.Event;

/***
 * Fired when a dungeon finishes creating a new level
 ***/
public class DungeonPostGenerateLevelEvent extends Event {
    @Override
    public String getEventName() {
        return "dungeon_post_generate_level";
    }

    public Level level;

    public DungeonPostGenerateLevelEvent(Level level) {
        this.level = level;
    }
}
