package textualmold9830;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Preferences {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static int challenges = 0;
    public static boolean onlineMode = true;
    public static String serverName = "Nik-MPPDJarServer";
    public static boolean useCustomRelay = false;
    public static String customRelayAddress = "";
    public static int customRelayPort = 0;
    public static float timeToSkipTurn = 10;
    public static boolean sharedHunger = true;
    public static int levelSize = 32;
    public static ITEM_COLLECTED_MESSAGE_MODE itemCollectedMessageMode;

    public static NO_CONNECTED_HERO_BEHAVIOUR noConnectedHeroBehaviour = NO_CONNECTED_HERO_BEHAVIOUR.PAUSE_ACTORS;
    public static String serverUUID = UUID.randomUUID().toString();

    public static void save() {
        try {
            Files.write(Path.of("config.json"), gson.toJson(new PreferencesData(challenges, onlineMode, serverName, useCustomRelay, customRelayAddress, customRelayPort, timeToSkipTurn,sharedHunger, levelSize, serverUUID)).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void load() {
        Path configPath = Path.of("config.json");
        if (Files.exists(configPath)){
            try {
                String data = new String(Files.readAllBytes(configPath));
                PreferencesData prefs = gson.fromJson(data, PreferencesData.class);
                challenges = prefs.challenges;
                onlineMode = prefs.onlineMode;
                serverName = prefs.serverName;
                useCustomRelay = prefs.useCustomRelay;
                customRelayAddress = prefs.customRelayAddress;
                customRelayPort = prefs.customRelayPort;
                timeToSkipTurn = prefs.timeToSkipTurn;
                sharedHunger = prefs.sharedHunger;
                levelSize = prefs.levelSize;
                serverUUID = prefs.serverUUID;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public enum NO_CONNECTED_HERO_BEHAVIOUR
    {
        STOP_SERVER,
        PAUSE_ACTORS,
        PROCESS_ACTORS
    }

    public enum ITEM_COLLECTED_MESSAGE_MODE
    {
        SHOW_FOR_ALL,
        SHOW_FOR_ALL_NOT_IMPORTANT,
        DONT_SHOW_FOR_ALL
    }
}
