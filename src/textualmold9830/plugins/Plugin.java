package textualmold9830.plugins;

import textualmold9830.plugins.events.*;

public interface Plugin {

    String getName();

    default String getVersion(){
        return "";
    };

    void initialize();

    default void shutdown(){}
    default void handleEvent(Event event) {
        switch (event.getEventName()){
            case "hero_do_action" -> handleHeroDoActionEvent((HeroDoActionEvent) event);
            case "hero_use_wand" -> handleHeroUseWandEvent((HeroUseWandEvent) event);
            case "dungeon_generate_level" -> handleDungeonGenerateLevelEvent((DungeonGenerateLevelEvent) event);
            case "char_die" -> handleCharDieEvent((CharDieEvent) event);
            case "hero_eat_food" -> handleHeroEatEvent((HeroEatFoodEvent) event);
        }
    }
    default void handleHeroDoActionEvent(HeroDoActionEvent event){}
    default void handleHeroUseWandEvent(HeroUseWandEvent event){}
    default String defaultConfig(){
        return "";
    }
    default void handleDungeonGenerateLevelEvent(DungeonGenerateLevelEvent event){}
    default void handleCharDieEvent(CharDieEvent event){}
    default void handleHeroEatEvent(HeroEatFoodEvent event){}
}
