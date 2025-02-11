package com.watabou.pixeldungeon.network;
// based on https://developer.android.com/training/connect-devices-wirelessly/nsd.html#java


import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.BuildConfig;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.utils.GLog;
import net.posick.mDNS.MulticastDNSService;
import net.posick.mDNS.ServiceInstance;
import net.posick.mDNS.ServiceName;
import org.xbill.DNS.Name;
import textualmold9830.Preferences;
import textualmold9830.plugins.PluginManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import static com.watabou.pixeldungeon.Dungeon.heroes;


public class Server extends Thread {
    public static ArrayList<String> textures = new ArrayList<>();
    public static PluginManager pluginManager = new PluginManager();

    //primitive vars
    public static String serviceName;
    protected static int localPort;
    public static boolean started = false;

    //network
    protected static ServerSocket serverSocket;
    protected static Server serverThread;
    protected static ClientThread[] clients = new ClientThread[0];
    protected static RelayThread relay;

    //NSD
    public static final String SERVICETYPE = "_mppd._tcp.local"; // _name._protocol //mppd=MultiPlayerPixelDungeon

    public static volatile RegListenerState regListenerState = RegListenerState.NONE;
    protected static final int TIME_TO_STOP = 3000; //ms
    protected static final int SLEEP_TIME = 100; // ms

    public static Thread serverStepThread;

    public static boolean startServerStepLoop() {
        if ((serverStepThread != null) && (serverStepThread.isAlive())) {
            return false;
        }
        {
            serverStepThread = new Thread() {
                @Override
                public void run() {
                    //
                    try {
                        while (!interrupted()) {
                            if (Game.instance != null) {
                                if ((Game.scene() instanceof GameScene)) {
                                    Game.instance.server_step();
                                } else {
                                    sleep(500);
                                }
                            } else {
                                sleep(500);
                            }
                        }
                    } catch (InterruptedException ignored) {

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (BuildConfig.STOP_SERVER_ON_EXCEPTION) {
                            pluginManager.shutdownPlugins();
                            System.exit(2);
                        }
                    }
                }
            };
            serverStepThread.setDaemon(true);
            serverStepThread.setName("Server Step Thread");
        }
        serverStepThread.start();
        return true;
    }

    public static boolean startServer() {
        if (started) {
            GLog.h("start when started: WTF?! WHO AND WHERE USED THIS?!");
            return false;
        }
        clients = new ClientThread[Settings.maxPlayers];
        serviceName = PixelDungeon.serverName() + "._mppd._tcp.local.";
        regListenerState = RegListenerState.NONE;
        if (!initializeServerSocket()) {
            return false;
        }
        try {
            registerService(localPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to start local discovery service");
        }

        started = true;
        serverThread = new Server();

        serverThread.start();

        return started;
    }

    public static boolean stopServer() {
        if (!started) {
            return true;
        }
        started = false;
        if (relay != null) {
            relay.interrupt();
            relay = null;
        }
        serverStepThread.interrupt();
        //ClientThread.sendAll(Codes.SERVER_CLOSED); //todo
        unregisterService();

        return true;
    }

    public static boolean parseActions() {
        boolean parsedAnything = false;
        for (ClientThread client : com.watabou.pixeldungeon.network.Server.clients) {
            if (client == null) {
                continue;
            }
            if (client.parse()) {
                parsedAnything = true;
            }
        }
        return parsedAnything;
    }

    public static void startClientThread(Socket client) throws IOException {
        synchronized (clients) {
            for (int i = 0; i <= clients.length; i++) {   //search not connected
                if (i == clients.length) { //If we test last and it's connected too
                    //todo use new json
                    new DataOutputStream(client.getOutputStream()).writeInt(com.watabou.pixeldungeon.network.Codes.SERVER_FULL);
                    client.close();
                } else if (clients[i] == null) {
                    synchronized (heroes) {
                        Hero emptyHero = null;
                        clients[i] = new ClientThread(i, client, emptyHero); //found
                    }
                    break;
                }
            }
        }
    }

    //Server thread
    public void run() {
        if (PixelDungeon.onlineMode()) {
            relay = new RelayThread();

            relay.start();
        }
        while (started) { //clients  listener
            Socket client;
            try {
                client = serverSocket.accept();  //accept connect
                startClientThread(client);
            } catch (IOException e) {
                if (!(e.getMessage().equals("Socket is closed"))) {  //"Socket is closed" means that client disconnected
                    GLog.h("IO exception:".concat(e.getMessage()));
                }
            }
        }
    }

    //NSD
    public static MulticastDNSService mDNSService;
    static boolean useNSD = false;
    static {
        try {
            if (!Preferences.onlineMode || getLocalIPAddress() != null) {
                mDNSService = new MulticastDNSService();
                useNSD = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServiceInstance service;

    protected static void registerService(int port) throws IOException {
            ServiceName serviceName = new ServiceName(Server.serviceName);
            Name hostname = new Name("mppd.local.");
            InetAddress[] addresses = new InetAddress[]{getLocalIPAddress()};
            String[] txtValues = new String[]{""};
            service = new ServiceInstance(serviceName, 0, 0, port, hostname, addresses, txtValues);
            ServiceInstance registeredService = mDNSService.register(service);
            if (registeredService != null) {
                System.out.println("Services Successfully Registered: \n\t" + registeredService);
            } else {
                System.err.println("Services Registration Failed!");
            }
    }
    public static void unregisterService() {
        if (!PixelDungeon.onlineMode()) {
            try {
                mDNSService.unregister(service);
            } catch (IOException e) {
                System.out.println("Failed to stop discovery service");
                e.printStackTrace();
            }
        }
    }

    protected static boolean initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try {
            serverSocket = new ServerSocket(0);
        } catch (Exception e) {
            return false;
        }
        // Store the chosen port.
        localPort = serverSocket.getLocalPort();
        return true;
    }
    public static InetAddress getLocalIPAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // We don't want loopback addresses
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                // Check for IPv4 (optional, but often desired)
                if (addr.getAddress().length == 4)
                return addr;
            }
        }
        return null; // No suitable address found
    }
    public static enum RegListenerState {NONE, UNREGISTERED, REGISTERED, REGISTRATION_FAILED, UNREGISTRATION_FAILED}
}
