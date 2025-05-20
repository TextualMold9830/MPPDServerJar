package textualmold9830;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;

public class Preferences {

    public static int challenges = 0;
    public static boolean onlineMode = true;
    public static String serverName;
    static {
        String id = UUID.randomUUID().toString();
        Random random = new Random();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            nameBuilder.append(id.charAt(random.nextInt(id.length())));
        }
        nameBuilder.append("-MPPDServerJar");
        serverName = nameBuilder.toString();
    }
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
        JSONObject config = new JSONObject();
        for (Field field: Preferences.class.getDeclaredFields()){
            try {
                config.put(field.getName(), field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Files.write(Path.of("config.json"), config.toString(4).getBytes());
        } catch (IOException e) {
            System.out.println("Failed to save config");
            throw new RuntimeException(e);
        }
    }
    public static void load() {
        Path configPath = Path.of("config.json");
        if (Files.exists(configPath)){
            try {
            JSONObject config = new JSONObject(new String(Files.readAllBytes(configPath)));
            for (Field field: Preferences.class.getDeclaredFields()){
                if (!field.isEnumConstant() && !field.isSynthetic() && Modifier.isStatic(field.getModifiers())){
                    Class<?> type = field.getType();
                    if(config.has(field.getName())) {
                        if (type == float.class) {
                            field.set(null,(float) config.getDouble(field.getName()));
                        } else if(type == double.class){
                            field.set(null, config.getDouble(field.getName()));
                        }
                        else if (!type.isEnum()) {
                            field.set(null, config.get(field.getName()));
                        } else {
                            Class<Enum> enumType = (Class<Enum>) type;
                            field.set(null, config.getEnum(enumType, field.getName()));
                        }
                    }
                }
            }
        } catch (Exception e){
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
