package com.watabou.pixeldungeon.network;

import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Settings;
import com.watabou.pixeldungeon.utils.GLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

import static com.watabou.pixeldungeon.network.ClientThread.CHARSET;


public class RelayThread extends Thread {
    private static int restartCount = 0;
    protected OutputStreamWriter writeStream;
    protected BufferedWriter writer;
    protected InputStreamReader readStream;
    private BufferedReader reader;
    protected Socket clientSocket;
    private Callback callback = null;

    public RelayThread(){
        this.callback = new Callback() {
            @Override
            public void onDisconnect() {
            };
        };
    }
    public RelayThread(Callback callback){
        this.callback = callback;
    }
    private static int getRelayPort(){
        if (!PixelDungeon.useCustomRelay()){
            return Settings.defaultRelayServerPort;
        }
        int port = PixelDungeon.customRelayPort();
       return (port != 0)? port: Settings.defaultRelayServerPort;
    }

    private static String getRelayAddress(){
        if (!PixelDungeon.useCustomRelay()){
            return Settings.defaultRelayServerAddress;
        }
        String address = PixelDungeon.customRelayAddress();
        return (!"".equals(address))? address : Settings.defaultRelayServerAddress;
    }

    public void run() {
        Socket socket = null;
        String relayServerAddress = getRelayAddress();
        try {
            socket = new Socket(relayServerAddress, getRelayPort());
        } catch (IOException e) {
            e.printStackTrace();
            this.callback.onDisconnect();
            return;
        }
        this.clientSocket = socket;
        try {
            writeStream = new OutputStreamWriter(
                    clientSocket.getOutputStream(),
                    Charset.forName(CHARSET).newEncoder()
            );
            readStream = new InputStreamReader(
                    clientSocket.getInputStream(),
                    Charset.forName(CHARSET).newDecoder()
            );
            reader = new BufferedReader(readStream);
            writer = new BufferedWriter(writeStream, 16384);


            JSONObject name = new JSONObject();
            name.put("action", "name");
            name.put("name", PixelDungeon.serverName());
            writer.write(name.toString());
            writer.write('\n');
            writer.flush();
            while (true) {
                String json = reader.readLine();
                if (json == null){
                    // we silence relay related messages for the first three times. We do not want confused users.
                    if (restartCount > 3) {
                        GLog.h("relay thread stopped");
                    }
                    socket.close();
                    this.callback.onDisconnect();
                    if (restartCount < 10) {
                        if (restartCount > 3) {
                            System.out.println("Restarting relay");
                        }
                        new RelayThread().start();
                        restartCount++;
                    } else {
                        System.out.println("Starting relay failed");
                    }
                    return;
                }
                JSONObject port_obj = new JSONObject(json);
                int port = port_obj.getInt("port");
                Socket client = new Socket(relayServerAddress, port);
                Server.startClientThread(client);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            restartCount = 0;
            GLog.h("relay thread stopped");
            this.callback.onDisconnect();
            System.out.println("restarting relay because of disconnect");
            new RelayThread().start();
            return;
        }
    }

    public interface Callback {
         void onDisconnect();
    }
}
