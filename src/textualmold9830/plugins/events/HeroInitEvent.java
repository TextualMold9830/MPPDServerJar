package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.actors.hero.Hero;
import textualmold9830.plugins.Event;

public class HeroInitEvent extends Event {
    public Hero hero;
    public boolean overrideCommon = false;
    public boolean overrideInitClass = false;
    public boolean tomeOfMastery;
    public HeroInitEvent(Hero hero) {
        this.hero = hero;
        tomeOfMastery = Badges.isUnlocked( hero.heroClass.masteryBadge() );
    }
}
