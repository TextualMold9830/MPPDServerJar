package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroAction;
import textualmold9830.plugins.Cancellable;
import textualmold9830.plugins.Event;

public class HeroDoActionEvent extends Event implements Cancellable {
    public Hero hero;
    public HeroAction action;
    boolean canceled = false;
    @Override
    public String getEventName() {
        return "hero_do_action";
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    public HeroDoActionEvent(Hero hero, HeroAction action) {
        this.hero = hero;
        this.action = action;
    }
}
