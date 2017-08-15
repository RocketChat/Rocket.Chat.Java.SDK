package io.rocketchat.livechat.middleware;

import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.livechat.callback.AgentListener;
import io.rocketchat.livechat.callback.MessageListener;
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

    private MessageListener.SubscriptionListener subscriptionListener;
    private AgentListener.AgentConnectListener agentConnectListener;
    private TypingListener typingListener;

    private ConcurrentHashMap<String, SubscribeListener> subcallbacks;

    public LiveChatStreamMiddleware() {
        subcallbacks = new ConcurrentHashMap<>();
    }

    private static SubType parse(String s) {
        if (s.equals("stream-room-messages")) {
            return SubType.STREAM_ROOM_MESSAGES;
        } else if (s.equals("stream-livechat-room")) {
            return SubType.STREAM_LIVECHAT_ROOM;
        } else {
            return SubType.NOTIFY_ROOM;
        }
    }

    public void subscribeRoom(MessageListener.SubscriptionListener subscription) {
        this.subscriptionListener = subscription;
    }

    public void subscribeLiveChatRoom(AgentListener.AgentConnectListener agentConnectListener) {
        this.agentConnectListener = agentConnectListener;
    }

    public void subscribeTyping(TypingListener callback) {
        typingListener = callback;
    }

    public void createSubCallbacks(String id, SubscribeListener callback) {
        if (callback != null) {
            subcallbacks.put(id, callback);
        }
    }

    public void processCallback(JSONObject object) {
        String s = object.optString("collection");
        JSONArray array = object.optJSONObject("fields").optJSONArray("args");

        switch (parse(s)) {
            case STREAM_ROOM_MESSAGES:
                if (subscriptionListener != null) {
                    LiveChatMessage liveChatMessage = new LiveChatMessage(array.optJSONObject(0));
                    String roomId = object.optJSONObject("fields").optString("eventName");
                    if (liveChatMessage.getMessagetype().equals(LiveChatMessage.MESSAGE_TYPE_CLOSE)) {
                        subscriptionListener.onAgentDisconnect(roomId, liveChatMessage);
                    } else {
                        subscriptionListener.onMessage(roomId, liveChatMessage);
                    }
                }
                break;
            case STREAM_LIVECHAT_ROOM:
                if (agentConnectListener != null) {
                    agentConnectListener.onAgentConnect(new AgentObject(array.optJSONObject(0).optJSONObject("data")));
                }
                break;
            case NOTIFY_ROOM:
                if (typingListener != null) {
                    typingListener.onTyping(object.optJSONObject("fields").optString("eventName"), array.optString(0), array.optBoolean(1));
                }
                break;
        }
    }

    public void processSubSuccess(JSONObject subObj) {
        if (subObj.optJSONArray("subs") != null) {
            String id = subObj.optJSONArray("subs").optString(0);
            if (subcallbacks.containsKey(id)) {
                SubscribeListener subscribeListener = subcallbacks.remove(id);
                subscribeListener.onSubscribe(true, id);
            }
        }
    }

    public enum SubType {
        STREAM_ROOM_MESSAGES,
        STREAM_LIVECHAT_ROOM,
        NOTIFY_ROOM
    }

}
