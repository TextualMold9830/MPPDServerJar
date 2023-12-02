package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Item;
import textualmold9830.plugins.Cancellable;
import textualmold9830.plugins.Event;

public class HeroCollectItemEvent extends Event implements Cancellable {
    boolean canceled = false;
    Item item;
    Hero hero;

    public HeroCollectItemEvent(Item item, Hero hero) {
        this.item = item;
        this.hero = hero;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
}
