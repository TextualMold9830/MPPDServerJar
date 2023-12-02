package textualmold9830;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Preferences {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static int challenges = 0;
    public static boolean onlineMode = true;
    public static String serverName = "Nik-MPPDJarServer";
    public static boolean useCustomRelay = false;
    public static String customRelayAdress = "";
    public static int customRelayPort = 0;
    public static boolean music = true;

    public static void save() {
        try {
            Files.write(Path.of("config.json"), gson.toJson(new PreferncesData(challenges, onlineMode, serverName, useCustomRelay, customRelayAdress, customRelayPort, music)).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void load() {
        Path configPath = Path.of("config.json");
        if (Files.exists(configPath)){
            try {
                String data = new String(Files.readAllBytes(configPath));
                PreferncesData prefs = gson.fromJson(data, PreferncesData.class);
                challenges = prefs.challenges;
                onlineMode = prefs.onlineMode;
                serverName = prefs.serverName;
                useCustomRelay = prefs.useCustomRelay;
                customRelayAdress = prefs.customRelayAdress;
                customRelayPort = prefs.customRelayPort;
                music = prefs.music;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
