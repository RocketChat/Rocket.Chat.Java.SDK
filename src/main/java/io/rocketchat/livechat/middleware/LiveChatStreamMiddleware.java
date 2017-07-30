package io.rocketchat.livechat.middleware;

import io.rocketchat.livechat.callback.AgentListener;
import io.rocketchat.livechat.callback.MessageListener;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.LiveChatMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 9/6/17.
 */

//This middleware consists of room SubType callback

public class LiveChatStreamMiddleware {

    public enum SubType {
        STREAMROOMMESSAGES,
        STREAMLIVECHATROOM,
        NOTIFYROOM
    }

    public static LiveChatStreamMiddleware middleware=new LiveChatStreamMiddleware();


    MessageListener.SubscriptionListener subscriptionListener;
    AgentListener.AgentConnectListener agentConnectListener;
    TypingListener typingListener;

    ConcurrentHashMap <String,Object[]> subcallbacks;


    LiveChatStreamMiddleware(){
        subcallbacks=new ConcurrentHashMap<>();
    }

    public static LiveChatStreamMiddleware getInstance(){
        return middleware;
    }

    public void subscribeRoom(MessageListener.SubscriptionListener subscription) {
        this.subscriptionListener = subscription;
    }

    public void subscribeLiveChatRoom(AgentListener.AgentConnectListener agentConnectListener) {
        this.agentConnectListener = agentConnectListener;
    }

    public void subscribeTyping(TypingListener callback){
        typingListener =callback;
    }

    public void createSubCallbacks(String id, SubscribeListener callback, SubType subscription){
        subcallbacks.put(id,new Object[]{callback,subscription});
    }

    public void processCallback(JSONObject object){
        String s = object.optString("collection");
        JSONArray array=object.optJSONObject("fields").optJSONArray("args");

        switch (parse(s)) {
            case STREAMROOMMESSAGES:
                if (subscriptionListener !=null) {
                    LiveChatMessage liveChatMessage = new LiveChatMessage(array.optJSONObject(0));
                    String roomId = object.optJSONObject("fields").optString("eventName");
                    if (liveChatMessage.getMessagetype().equals(LiveChatMessage.MESSAGE_TYPE_CLOSE)) {
                        subscriptionListener.onAgentDisconnect(roomId, liveChatMessage);
                    } else {
                        subscriptionListener.onMessage(roomId, liveChatMessage);
                    }
                }
                break;
            case STREAMLIVECHATROOM:
                if (agentConnectListener !=null) {
                    agentConnectListener.onAgentConnect(new AgentObject(array.optJSONObject(0).optJSONObject("data")));
                }
                break;
            case NOTIFYROOM:
                if (typingListener !=null) {
                    typingListener.onTyping(object.optJSONObject("fields").optString("eventName"), array.optString(0), array.optBoolean(1));
                }
                break;
        }
    }

    public void processSubSuccess(JSONObject subObj){
        if (subObj.optJSONArray("subs")!=null) {
            String id = subObj.optJSONArray("subs").optString(0);
            if (subcallbacks.containsKey(id)) {
                Object object[] = subcallbacks.remove(id);
                SubscribeListener subscribeListener = (SubscribeListener) object[0];
                SubType subscription = (SubType) object[1];
                subscribeListener.onSubscribe(subscription, id);
            }
        }
    }

    public static SubType parse(String s){
        if (s.equals("stream-room-messages")){
            return SubType.STREAMROOMMESSAGES;
        }else if (s.equals("stream-livechat-room")){
            return SubType.STREAMLIVECHATROOM;
        }else {
            return SubType.NOTIFYROOM;
        }
    }

}
