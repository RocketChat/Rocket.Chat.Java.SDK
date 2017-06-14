package io.rocketchat.core;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import io.rocketchat.common.network.Socket;
import io.rocketchat.core.rpc.BasicRPC;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */
public class RocketChatAPI extends Socket {

    AtomicInteger integer;
    String sessionId;
    WebSocketAdapter adapter;

    public RocketChatAPI(String url) {
        super(url);
        adapter=getAdapter();
        integer=new AtomicInteger(1);
    }

    public void connect(){
        createSocket();
        ws.addListener(adapter);
        super.connect();
    }

    WebSocketAdapter getAdapter() {
        return new WebSocketAdapter(){
            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                JSONObject object = new JSONObject(text);

                if (object.has("server_id")) {
                    websocket.sendText(BasicRPC.ConnectObject());
                } else {

                    if (object.optString("msg").equals("ping")) {
                        websocket.sendText("{\"msg\":\"pong\"}");
                    } else if (object.optString("msg").equals("connected")) {
                        sessionId = object.optString("session");
                        System.out.println("session id is "+sessionId);
                    }
//
                    System.out.println("Message is " + text);
                }
                super.onTextMessage(websocket, text);
            }
        };
    }
}
