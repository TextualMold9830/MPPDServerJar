package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.food.Food;
import textualmold9830.plugins.Event;

public class HeroEatFoodEvent extends Event {
    public Hero hero;
    public Food food;

    public HeroEatFoodEvent(Hero hero, Food food) {
        this.hero = hero;
        this.food = food;
    }
}
