package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.Char;
import textualmold9830.plugins.Event;

public class CharDieEvent extends Event {
    boolean cancelled = false;
    public Char character;
    public Object cause;

    public CharDieEvent(Char character, Object cause) {
        this.character = character;
        this.cause = cause;
    }


}
