package textualmold9830;

public class PreferncesData {
    public int challenges = 0;

    public  boolean onlineMode = true;
    public  String serverName = "Nik-MPPDJarServer";
    public  boolean useCustomRelay = false;
    public  String customRelayAddress = "";
    public  int customRelayPort = 0;
    public float timeToSkipTurn = 10;
    public boolean sharedHunger = false;

    public PreferncesData() {
    }

    public PreferncesData(int challenges, boolean onlineMode, String serverName, boolean useCustomRelay, String customRelayAddress, int customRelayPort, float timeToSkipTurn) {
        this.challenges = challenges;
        this.onlineMode = onlineMode;
        this.serverName = serverName;
        this.useCustomRelay = useCustomRelay;
        this.customRelayAddress = customRelayAddress;
        this.customRelayPort = customRelayPort;
    }
}
