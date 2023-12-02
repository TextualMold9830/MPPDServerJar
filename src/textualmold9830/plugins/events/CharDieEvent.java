package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.Char;
import textualmold9830.plugins.Cancellable;
import textualmold9830.plugins.Event;

public class CharDieEvent extends Event implements Cancellable {
    boolean cancelled = false;
    Char character;
    Object cause;

    public CharDieEvent(Char character , Object cause) {
        this.character = character;
        this.cause = cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
     cancelled = cancel;
    }
}
