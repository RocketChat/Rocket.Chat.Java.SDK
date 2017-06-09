package io.rocketchat.livechat.middleware;

import io.rocketchat.livechat.callbacks.AgentCallback;
import io.rocketchat.livechat.callbacks.MessageCallback;
import io.rocketchat.livechat.callbacks.TypingCallback;
import io.rocketchat.livechat.models.AgentObject;
import io.rocketchat.livechat.models.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */

//This middleware consists of room subscription callbacks

public class LiveChatStreamMiddleware {

    public static LiveChatStreamMiddleware middleware=new LiveChatStreamMiddleware();

    MessageCallback messageCallback;
    AgentCallback agentCallback;
    TypingCallback typingCallback;

    LiveChatStreamMiddleware(){
    }

    public static LiveChatStreamMiddleware getInstance(){
        return middleware;
    }

    public void subscribeRoom(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void subscribeLiveChatRoom(AgentCallback agentCallback) {
        this.agentCallback = agentCallback;
    }

    public void subscribeTyping(TypingCallback callback){
        typingCallback=callback;
    }

    public void processCallback(JSONObject object){
        String s = object.optString("collection");
        JSONArray array=object.optJSONObject("fields").optJSONArray("args");
        if (s.equals("stream-room-messages")) {
            messageCallback.call(object.optJSONObject("fields").optString("eventName"),new MessageObject(array.optJSONObject(0)));
        }else if (s.equals("stream-livechat-room")){
            agentCallback.call(new AgentObject(array.optJSONObject(0)));
        }else{
            typingCallback.call(object.optJSONObject("fields").optString("eventName"),array.optString(0),array.optBoolean(1));
        }
    }

}
