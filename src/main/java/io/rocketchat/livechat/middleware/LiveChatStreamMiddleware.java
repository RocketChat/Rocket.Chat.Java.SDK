package io.rocketchat.livechat.middleware;

import io.rocketchat.livechat.callback.AgentCallback;
import io.rocketchat.livechat.callback.MessageCallback;
import io.rocketchat.livechat.callback.SubscribeCallback;
import io.rocketchat.livechat.callback.TypingCallback;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 9/6/17.
 */

//This middleware consists of room subscriptiontype callback

public class LiveChatStreamMiddleware {

    public enum subscriptiontype {
        STREAMROOMMESSAGES,
        STREAMLIVECHATROOM,
        NOTIFYROOM
    }

    public static LiveChatStreamMiddleware middleware=new LiveChatStreamMiddleware();


    MessageCallback messageCallback;
    AgentCallback agentCallback;
    TypingCallback typingCallback;

    ConcurrentHashMap <String,Object[]> subcallbacks;


    LiveChatStreamMiddleware(){
        subcallbacks=new ConcurrentHashMap<>();
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

    public void createSubCallbacks(String id, SubscribeCallback callback, subscriptiontype subscription){
        subcallbacks.put(id,new Object[]{callback,subscription});
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

    public void processSubSuccess(JSONObject subObj){
        if (subObj.optJSONArray("subs")!=null) {
            String id = subObj.optJSONArray("subs").optString(0);
            if (subcallbacks.containsKey(id)) {
                Object object[] = subcallbacks.remove(id);
                SubscribeCallback subscribeCallback = (SubscribeCallback) object[0];
                subscriptiontype subscription = (subscriptiontype) object[1];
                subscribeCallback.onSubscribe(subscription, id);
            }
        }
    }

}
