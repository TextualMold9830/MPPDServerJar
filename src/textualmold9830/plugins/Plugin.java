package textualmold9830.plugins;

import textualmold9830.plugins.events.HeroDoActionEvent;
import textualmold9830.plugins.events.HeroUseWandEvent;

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
        }
    }
    default void handleHeroDoActionEvent(HeroDoActionEvent event){}
    default void handleHeroUseWandEvent(HeroUseWandEvent event){}
    default String defaultConfig(){
        return "";
    }
}
