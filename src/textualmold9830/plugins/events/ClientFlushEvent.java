package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.network.ClientThread;
import org.json.JSONObject;
import textualmold9830.plugins.Event;

import java.util.concurrent.atomic.AtomicReference;

public class ClientFlushEvent extends Event {
    public AtomicReference<JSONObject> dataRef;
    public ClientThread client;
    public ClientFlushEvent(ClientThread client, AtomicReference<JSONObject> dataRef) {
        this.client = client;
        this.dataRef = dataRef;
    }
}
