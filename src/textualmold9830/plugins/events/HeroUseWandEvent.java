package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.wands.Wand;
import textualmold9830.plugins.Cancellable;
import textualmold9830.plugins.Event;

public class HeroUseWandEvent extends Event implements Cancellable {
    @Override
    public String getEventName() {
        return "hero_use_wand";
    }

    boolean canceled = false;
    public Hero hero;
    public Wand wand;
    public int cell;
    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
    canceled = cancel;
    }

    public HeroUseWandEvent(Hero hero, Wand wand, int cell) {
        this.hero = hero;
        this.wand = wand;
        this.cell = cell;
    }
}
