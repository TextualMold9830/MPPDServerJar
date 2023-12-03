package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.levels.Level;
import textualmold9830.plugins.Event;

public class DungeonGenerateLevelEvent extends Event {
    public int depth;
    public Level level;

    public DungeonGenerateLevelEvent(int depth, Level level) {
        this.depth = depth;
        this.level = level;
    }

    @Override
    public String getEventName() {
        return "dungeon_generate_level";
    }
}
