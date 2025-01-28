package textualmold9830.plugins.events;

import com.watabou.pixeldungeon.network.ClientThread;
import org.json.JSONObject;
import textualmold9830.plugins.Event;

public class ClientParseEvent extends Event {
    public JSONObject data;
    public ClientThread client;
    public ClientParseEvent(ClientThread client, JSONObject data) {
        this.client = client;
        this.data = data;
    }
}
