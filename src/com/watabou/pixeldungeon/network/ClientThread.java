package com.watabou.pixeldungeon.network;

import com.nikita22007.multiplayer.utils.Log;
import com.watabou.pixeldungeon.BuildConfig;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroClass;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.utils.GLog;
import com.watabou.pixeldungeon.utils.Utils;
import com.watabou.utils.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import textualmold9830.plugins.events.ClientFlushEvent;
import textualmold9830.plugins.events.ClientParseEvent;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.watabou.pixeldungeon.Dungeon.heroes;

public class ClientThread {

    public static final String CHARSET = "UTF-8";

    protected OutputStreamWriter writeStream;
    protected BufferedWriter writer;
    protected InputStreamReader readStream;
    private BufferedReader reader;

    protected int threadID;

    protected final Socket clientSocket;

    public Hero clientHero;

    protected final NetworkPacket packet = new NetworkPacket();

    @NotNull
    volatile private CompletableFuture<JSONObject> jsonCall;

    public ClientThread(int ThreadID, Socket clientSocket, @Nullable Hero hero) {
        clientHero = hero;
        if (hero != null){
            hero.networkID = threadID;
        }
        this.clientSocket = clientSocket;
        try {
            writeStream = new OutputStreamWriter(
                    clientSocket.getOutputStream(),
                    Charset.forName(CHARSET).newEncoder()
            );
            readStream = new InputStreamReader(
                    clientSocket.getInputStream(),
                    Charset.forName(CHARSET).newDecoder()
            );
            this.threadID = ThreadID;
            reader = new BufferedReader(readStream);
            writer = new BufferedWriter(writeStream, 16384);
            packet.packAndAddServerUUID();
            flush();
        } catch (IOException e) {
            GLog.n(e.getMessage());
            disconnect();
            return;
        }
        if (clientHero != null){
            sendInitData();
        }
        updateTask();
    }
    protected void updateTask() {
        if ((jsonCall == null) || (jsonCall.isDone())) {
            new CompletableFuture<String>();
            jsonCall = CompletableFuture.supplyAsync(this::runTask);
            jsonCall.whenComplete((result, exception) -> {
                GameScene.notifySelf();
            });
        }
    }
    //@Override
    @Nullable
    public JSONObject runTask() {
        if (clientSocket.isClosed()) {
            return null;
        }
        try {
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            return new JSONObject(line);
        } catch (JSONException e) {
            Log.e("ParseThread", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("ParseThread", e.getMessage());
            return null;
        }
    }

    public void parse(@NotNull String json) throws JSONException {
        JSONObject data = new JSONObject(json);
        parse(data);
    }

    public void parse(@NotNull JSONObject data) throws JSONException {
        Server.pluginManager.fireEvent(new ClientParseEvent(this, data));
        System.out.println("client: " + data.toString(4));
        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
            String token = it.next();
            try {
                switch (token) {
                    //Level block
                    case ("hero_class"): {
                        if (clientHero == null) {
                            InitPlayerHero(data.getString(token), data.optString("uuid", null));
                        }
                        break;
                    }
                    case ("cell_listener"): {
                        Integer cell = data.getInt(token);
                        if (clientHero.cellSelector != null) {
                            if (clientHero.cellSelector.getListener() != null) {
                                if (cell != -1) {
                                    clientHero.cellSelector.getListener().onSelect(cell);
                                } else {
                                    clientHero.cellSelector.cancel();
                                }
                                GameScene.ready(clientHero);
                            }
                        }
                        break;
                    }
                    case ("action"): {
                        JSONObject actionObj = data.getJSONObject(token);
                        if (actionObj == null) {
                            GLog.n("Empty action object");
                            break;
                        }
                        String action = actionObj.getString("action_name");
                        if ((action == null) || (action.equals(""))) {
                            GLog.n("Empty action");
                            break;
                        }
                        List<Integer> slot = Utils.JsonArrayToListInteger(actionObj.getJSONArray("slot"));
                        if ((slot == null) || slot.isEmpty()) {
                            GLog.n("Empty slot: %s", slot);
                            break;
                        }
                        Item item = clientHero.belongings.getItemInSlot(slot);
                        if (item == null) {
                            GLog.n("No item in this slot. Slot: %s", slot);
                            break;
                        }
                        action = action.toLowerCase(Locale.ROOT);
                        boolean did_something = false;
                        for (String item_action : item.actions(clientHero)) {
                            if (item_action.toLowerCase(Locale.ROOT).equals(action)) {
                                did_something = true;
                                item.execute(clientHero, item_action);
                                break;
                            }
                        }
                        if (!did_something) {
                            GLog.n("No such action in actions list. Action: %s", action);
                            break;
                        }
                        break;
                    }
                    case "window": {
                        JSONObject resObj = data.getJSONObject(token);
                        Window.OnButtonPressed(
                                clientHero,
                                resObj.getInt("id"),
                                resObj.getInt("button"),
                                resObj.optJSONObject("result")
                        );
                        break;
                    }
                    case "toolbar_action": {
                        JSONObject actionObj = data.getJSONObject(token);
                        switch (actionObj.getString("action_name").toUpperCase(Locale.ENGLISH)) {
                            case "SLEEP":
                            case "REST": {
                                clientHero.rest(true);
                                break;
                            }
                            case "WAIT": {
                                clientHero.rest(false);
                                break;
                            }
                            case "SEARCH": {
                                clientHero.search(true);
                                break;
                            }
                            default:
                                Log.e("Bat toolbar action: %s. Client: %d", token, threadID);
                                break;
                        }
                        break;
                    }
                    case "uuid":
                        //already parsed
                        break;
                    case "chat": {
                        if (clientHero == null) {
                            break;
                        }
                        String text = data.getJSONObject(token).optString("message", null);
                        if (text == null) {
                            text = data.getJSONObject(token).optString("text", "");
                        }
                        if (text.isBlank()) {
                            break;
                        }
                        GLog.i("%s: %s", clientHero.name,  text.trim());
                        break;
                    }
                    default: {
                        GLog.n("Server: Bad token: %s", token);
                        break;
                    }
                }
            } catch (JSONException e) {
                assert false;
                GLog.n(String.format("JSONException in ThreadID:%s; Message:%s", threadID, e.getMessage()));
            }
        }
    }


    public boolean parse() {
        if (!jsonCall.isDone()) {
            return false;
        }
        try {
            JSONObject jsonObject = jsonCall.get();
            if (jsonObject == null){
                disconnect();
                return false;
            }
            updateTask();
            try {
                parse(jsonObject);
            } catch (JSONException e) {
                PixelDungeon.reportException(e);
                GLog.n(e.getStackTrace().toString());
                disconnect();
                return false;
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    //network functions
    protected void flush() {
        try {
            synchronized (packet.dataRef) {
                if (packet.dataRef.get().length() == 0) {
                    return;
                }
                Server.pluginManager.fireEvent(new ClientFlushEvent(this, packet.dataRef));
                if (BuildConfig.DEBUG) {
                    try {
                        Log.i("flush", "clientID: " + threadID + " data:" + packet.dataRef.get().toString(4));
                    } catch (JSONException ignored) {
                    }
                }
                synchronized (writer) {
                    writer.write(packet.dataRef.get().toString());
                    writer.write('\n');
                    writer.flush();
                }
                packet.clearData();
            }
        } catch (IOException e) {
            Log.e(String.format("ClientThread%d", threadID), String.format("IOException in threadID %s. Message: %s", threadID, e.getMessage()));
            disconnect();
        } catch (StackOverflowError e) {
            Log.e("st", "st", e);
        }
    }

    //some functions
    protected void InitPlayerHero(String className, String UUID) {
        HeroClass curClass;
        try {
            curClass = HeroClass.valueOf(className.toUpperCase());
        } catch (IllegalArgumentException e) {
            if (!className.equals("random")) { //classID==0 is random class, so it  is not error
                GLog.w("Incorrect class:%s; threadID:%s", className, threadID);
            }
            curClass = Random.element(HeroClass.values());
        }
        Hero newHero = null;
        boolean heroFound = false;
        if (UUID != null){
            Optional<Hero> optHero = Dungeon.loadHero(UUID);
            if (optHero.isPresent()) {
                newHero = optHero.get();
                heroFound = true;
                //System.out.println("Found hero with UUID: " + UUID);
            }
        }
        if (!heroFound){
            newHero = new Hero();
        }
        clientHero = newHero;
        if (!heroFound) {
            newHero.live();

            curClass.initHero(newHero);
            if (newHero.level == null){
                Dungeon.switchLevel(Dungeon.defaultLevelIDForCurDepth(), newHero);
            }
            newHero.pos = Dungeon.GetPosNear(newHero.level, newHero.level.entrance);

            newHero.updateSpriteState();
        }
            if (newHero.pos == -1) {
                newHero.pos = newHero.level.entrance; //todo  FIXME
            }
            Actor.add(newHero, newHero.level);
            Actor.occupyCell(newHero);
            newHero.getSprite().place(newHero.pos);
                synchronized (heroes) { //todo fix it. It is not work
                     for (int i = 0; i < heroes.length; i++) {
                         if (heroes[i] == null) {
                             heroes[i] = newHero;
                             newHero.networkID = threadID;
                             newHero.name = "Player" + i;
                             break;
                }
            }
                     if (newHero.networkID == -1) {
                         throw new RuntimeException("Can not find place for hero");
                     }
                }
        GameScene.addHeroSprite(newHero);
        newHero.next();
        sendInitData();
        SendData.sendInterLevelSceneFadeOut(clientHero.networkID);
    }

    protected void addCharToSend(@NotNull Char ch) {
        synchronized (packet) {
            packet.packAndAddActor(ch, ch == clientHero);
        }
        //todo SEND TEXTURE
    }

    public void addAllCharsToSend() {
        for (Actor actor : Actor.all().get(clientHero.level)) {
            if (actor instanceof Char) {
                addCharToSend((Char) actor);
            }
        }
    }

    public void addBadgeToSend(String badgeName, int badgeLevel) {
        packet.packAndAddBadge(badgeName, badgeLevel);
    }

    //send primitives
    @Deprecated
    public void sendCode(int code) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, boolean Data) {

        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, byte Data) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, int Data) {
    }

    //send arrays
    @Deprecated
    public void send(int code, boolean[] DataArray) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, byte[] DataArray) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, int[] DataArray) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, int var1, String message) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    @Deprecated
    public void send(int code, String message) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    //send_serelliased_data
    @Deprecated
    public void sendData(int code, byte[] data) {
        assert false : "removed_code";
        GLog.n("removed code");
    }

    //send to all
    @Deprecated
    public static <T> void sendAll(int code) {
        for (int i = 0; i < Server.clients.length; i++) {
            Server.clients[i].sendCode(code);
        }
    }

    @Deprecated
    public static void sendAll(int code, int data) {
        for (int i = 0; i < Server.clients.length; i++) {
            if (Server.clients[i] != null) {
                Server.clients[i].send(code, data);
            }
        }
    }
    boolean disconnected = false;
    public void disconnect() {
        if (!disconnected) {
            disconnected = true;
            try {
                clientSocket.close(); //it creates exception when we will wait client data
            } catch (Exception ignore) {
            }
            if (clientHero != null) {
                clientHero.networkID = -1;
                clientHero.next();
                Dungeon.removeHero(clientHero);
            }
            Server.clients[threadID] = null;
            readStream = null;
            writeStream = null;
            jsonCall.cancel(true);
            GLog.n("player " + threadID + " disconnected");
            GameScene.notifySelf();
        }
    }

    private void sendInitData() {
        Server.textures.forEach(this::sendTexture);
        packet.packAndAddLevel(clientHero.level, clientHero);
        packet.packAndAddHero(clientHero);
        packet.packAndAddDepth(Dungeon.depth);
        packet.packAndAddIronKeysCount(clientHero.belongings.updateIronKeysCountVisual(false));
        packet.addInventoryFull(clientHero);
        addAllCharsToSend();

        Dungeon.observe(clientHero, false);
        packet.packAndAddVisiblePositions(clientHero.fieldOfView);
        //TODO send all  information

        flush();

        packet.packAndAddInterlevelSceneState("fade_out", null);
        flush();
    }
    private void sendTexture(String textureData){
        packet.packAndAddRawTextures(textureData);
        flush();
    }
}
