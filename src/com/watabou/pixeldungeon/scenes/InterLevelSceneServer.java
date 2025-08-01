package com.watabou.pixeldungeon.scenes;

import com.watabou.noosa.Game;
import com.nikita22007.multiplayer.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.Statistics;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.items.Generator;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.LobbyLevel;
import com.watabou.pixeldungeon.levels.RegularLevel;
import com.watabou.pixeldungeon.levels.features.Chasm;
import com.watabou.pixeldungeon.network.SendData;
import com.watabou.pixeldungeon.network.Server;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.windows.WndStory;
import com.watabou.utils.Random;

import org.jetbrains.annotations.Nullable;
import textualmold9830.plugins.events.DungeonPostGenerateLevelEvent;

import java.io.IOException;

import static com.watabou.pixeldungeon.Dungeon.*;
import static com.watabou.pixeldungeon.levels.Level.getNearClearCell;

public class InterLevelSceneServer {
    private static final float TIME_TO_FADE = 0.3f;

    private static final String TXT_DESCENDING	= "Descending...";
    private static final String TXT_ASCENDING	= "Ascending...";
    private static final String TXT_LOADING		= "Loading...";
    private static final String TXT_RESURRECTING= "Resurrecting...";
    private static final String TXT_RETURNING	= "Returning...";
    private static final String TXT_FALLING		= "Falling...";
    private static final String TXT_INCORRECT_MODE = "Incorrect Interlevel scene mode";

    private static final String ERR_FILE_NOT_FOUND	= "File not found. For some reason.";
    private static final String ERR_GENERIC			= "Something went wrong..."	;

    private static final String TXT_WELCOME			= "Welcome to the level %d of Pixel Dungeon!";
    private static final String TXT_WELCOME_BACK	= "Welcome back to the level %d of Pixel Dungeon!";
    private static final String TXT_NIGHT_MODE		= "Be cautious, since the dungeon is even more dangerous at night!";

    private static final String TXT_CHASM	= "Your steps echo across the dungeon.";
    private static final String TXT_WATER	= "You hear the water splashing around you.";
    private static final String TXT_GRASS	= "The smell of vegetation is thick in the air.";
    private static final String TXT_SECRETS	= "The atmosphere hints that this floor hides many secrets.";

    private static void ShowStoryIfNeed(int depth)
    {
        if (Statistics.deepestFloor>=depth){return;}
        switch (depth) { //Dungeon.depth
            case 1:
                SendData.sendWindowStory( WndStory.ID_SEWERS );
                break;
            case 6:
                SendData.sendWindowStory( WndStory.ID_PRISON );
                break;
            case 11:
                SendData.sendWindowStory( WndStory.ID_CAVES );
                break;
            case 16:
                SendData.sendWindowStory( WndStory.ID_METROPOLIS );
                break;
            case 22:
                SendData.sendWindowStory( WndStory.ID_HALLS );
                break;
        }
    /*    if (Dungeon.hero.isAlive() && Dungeon.depth != 22) {
            Badges.validateNoKilling();
        }*/
    }

    private static void sendMessage(Level level, boolean ascend){
        if (ascend) {
            if (Dungeon.depth < Statistics.deepestFloor) {
                GLog.h( TXT_WELCOME_BACK, Dungeon.depth );
            } else {
                GLog.h( TXT_WELCOME, Dungeon.depth );
                Sample.INSTANCE.play( Assets.SND_DESCEND );
            }
            switch (level.feeling) {
                case CHASM:
                    GLog.w( TXT_CHASM );
                    break;
                case WATER:
                    GLog.w( TXT_WATER );
                    break;
                case GRASS:
                    GLog.w( TXT_GRASS );
                    break;
                default:
            }
            if (level instanceof RegularLevel &&
                    ((RegularLevel) level).secretDoors > Random.IntRange( 3, 4 )) {
                GLog.w( TXT_SECRETS );
            }
            if (Dungeon.nightMode && !Dungeon.bossLevel(Dungeon.depth)) {
                GLog.w( TXT_NIGHT_MODE );
            }

        }
    }

    public static void descend(@Nullable Hero hero)  {// спуск
        try {
            Generator.reset();
            if (hero != null) {
                SendData.sendInterLevelScene(hero.networkID, "DESCEND", true);
            } else {
                SendData.sendInterLevelSceneForAll("DESCEND", true);
            }
            Actor.fixTime();
            if (Dungeon.depth > 0) {
                if (hero != null) {
                    Dungeon.saveLevel(hero.level);
                }
            }

            Level level;
            if (hero != null) {
                level = getNextLevel(hero.level);
            } else {
                level = new LobbyLevel();
                level.depth = 0;
                level.levelID = Dungeon.DUNGEON_LEVEL_PREFIX + level.depth;
                level.descendDestinationID = DUNGEON_LEVEL_PREFIX + level.depth + 1;
                level.create();
            }
            Dungeon.addLoadedLevel(level);
            if (hero == null) {
                Dungeon.switchLevel(level);
                SendData.sendInterLevelSceneFadeOutForAll();
            } else {
                Dungeon.switchLevel(level.levelID, level.entrance, hero);
                SendData.sendInterLevelSceneFadeOut(hero.networkID);

            }
            ShowStoryIfNeed(level.depth);
            sendMessage(level, false);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }
    public static  void  fall(Hero  hero){
     fall(hero,false);
    }
    public static void fall(Hero hero, boolean fallIntoPit) {

        try {
            Generator.reset();
            SendData.sendInterLevelSceneForAll("FALL");
            Actor.fixTime();
            Dungeon.saveLevel(hero.level);

            Level level;
            level = getNextLevel(hero.level);
            Dungeon.switchLevel(level.levelID, fallIntoPit ? level.pitCell() : level.randomRespawnCell(), hero);

            SendData.sendInterLevelSceneFadeOutForAll();
            for (Hero hero_ : heroes) {
                if (hero_ != null && hero.isAlive()) {
                    Chasm.heroLand(hero_);
                }
            }

            ShowStoryIfNeed(Dungeon.depth);
            sendMessage(level, false);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }
    private static Level getNextLevel(Level origin) throws IOException {


            Level level = Dungeon.newLevel();
            level.levelID = origin.descendDestinationID;
            level.ascendDestinationID = origin.levelID;
            level.descendDestinationID = origin.levelID.replaceAll("\\d","") + origin.depth+1;
            DungeonPostGenerateLevelEvent event = new DungeonPostGenerateLevelEvent(level);
            Server.pluginManager.fireEvent(event);
            level.levelID = Dungeon.defaultLevelIDForCurDepth();
            level = event.level;
            return level;
    }

    public static void ascend(Hero hero) {
        try {
            Dungeon.saveLevel(hero.level);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Generator.reset();
            SendData.sendInterLevelSceneForAll("ASCEND");
            Actor.fixTime();
            Dungeon.depth--;
            Level level = Dungeon.loadLevel(hero.level.ascendDestinationID);
            Dungeon.switchLevel(level.levelID, level.exit, hero);

            SendData.sendInterLevelSceneFadeOutForAll();
            sendMessage(level, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }

    public static void returnTo(String levelID, int pos, Hero  hero) {
        try {
            Generator.reset();
            if (hero.level.levelID != levelID) {
                SendData.sendInterLevelSceneForAll("RETURN");
                Actor.fixTime();
                Dungeon.saveLevel(hero.level);
                Level level = Dungeon.loadLevel(levelID);
                Dungeon.switchLevel(level.levelID, pos, hero);
                SendData.sendInterLevelSceneFadeOutForAll();
                sendMessage(level, true);
            } else {
                //TODO: check this
                hero.pos = getNearClearCell(hero.level, pos);
            }
            WandOfBlink.appear(hero, hero.pos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }

    public static void restore() { //when loading from save

        try {
            Generator.reset();
            Actor.fixTime();

            GLog.wipe();

            Dungeon.loadGame(true);
            if (Dungeon.depth == -1) {
                Dungeon.depth = Statistics.deepestFloor;
                Dungeon.switchLevel(Dungeon.loadLevel(Dungeon.defaultLevelIDForCurDepth()));
            } else {
                Level level = Dungeon.loadLevel(Dungeon.defaultLevelIDForCurDepth());
                Dungeon.switchLevel(level);
            }
        }catch (IOException  e){
            throw new RuntimeException(e);
        }
        Game.switchScene( GameScene.class );
    }

    @SuppressWarnings("fallthrough")
    public static void resurrect(Hero hero)  { //respawn by ankh

        Generator.reset();
        SendData.sendInterLevelSceneForAll("RESURRECT");
        Actor.fixTime();
        switch (Settings.resurrectMode){
            case RESET_LEVEL: {
                if (Dungeon.bossLevel(Dungeon.depth)) {
                    hero.resurrect(Dungeon.depth);
                    Dungeon.depth--;
                    Level level = Dungeon.newLevel();
                    Dungeon.switchLevelToAll(level, level.entrance);
                } else {
                    hero.resurrect(-1);
                    Dungeon.resetLevel(hero.level);
                }
            }
            case RESPAWN_HERO:
            {
                Dungeon.switchLevel(hero.level.levelID ,hero.level.entrance, hero);
            }
        }
        WandOfBlink.appear(hero,hero.pos);
        SendData.sendInterLevelSceneFadeOutForAll();
        sendMessage(hero.level,false);
        Game.switchScene( GameScene.class );
    }

}
