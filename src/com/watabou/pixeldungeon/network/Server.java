package com.watabou.pixeldungeon.network;
// based on https://developer.android.com/training/connect-devices-wirelessly/nsd.html#java


import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.utils.GLog;
import textualmold9830.plugins.PluginManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
                                    sleep(0);
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
                        System.exit(2);
                    }
                }
            };
            serverStepThread.setDaemon(true);
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
        serviceName = PixelDungeon.serverName();
        regListenerState = RegListenerState.NONE;
        if (!initializeServerSocket()) {
            return false;
        }
        registerService(localPort);

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
                        for (Hero hero : heroes) {
                            if (hero == null) {
                                continue;
                            }
                            if (hero.networkID != -1) {
                                continue;
                            }
                            emptyHero = hero;
                            break;
                        }
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
            }
            relay.start();

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
    protected static void registerService(int port) {
        // Create the NsdServiceInfo object, and populate it.

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
    }

    public static void unregisterService() {
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
    public static enum RegListenerState {NONE, UNREGISTERED, REGISTERED, REGISTRATION_FAILED, UNREGISTRATION_FAILED}
}
