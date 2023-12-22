package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.food.Food;
import textualmold9830.plugins.Cancellable;
import textualmold9830.plugins.Event;

public class HeroEatFoodEvent extends Event implements Cancellable {
    public Hero hero;
    public Food food;
    public float energy;
    private boolean canceled = false;

    public HeroEatFoodEvent(Hero hero, Food food, float energy) {
        this.hero = hero;
        this.food = food;
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
