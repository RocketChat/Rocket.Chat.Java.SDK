package com.rocketchat.livechat.internal.middleware;

import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.livechat.callback.AgentCallback;
import com.rocketchat.livechat.callback.MessageListener;
import com.rocketchat.livechat.model.AgentObject;
import com.rocketchat.livechat.model.LiveChatMessage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */

//This middleware consists of room SubType callback

public class LiveChatStreamMiddleware {

    private final Moshi moshi;

    private MessageListener.SubscriptionListener subscriptionListener;
    private AgentCallback.AgentConnectListener agentConnectListener;
    private TypingListener typingListener;

    private ConcurrentHashMap<String, SubscribeListener> subcallbacks;

    public LiveChatStreamMiddleware(Moshi moshi) {
        this.moshi = moshi;
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

    public void subscribeLiveChatRoom(AgentCallback.AgentConnectListener agentConnectListener) {
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
                    try {
                        JSONObject message = array.optJSONObject(0);
                        LiveChatMessage liveChatMessage = getMessageAdapter().fromJson(message.toString());
                        String roomId = object.optJSONObject("fields").optString("eventName");
                        if (liveChatMessage.type().equals(LiveChatMessage.MESSAGE_TYPE_CLOSE)) {
                            subscriptionListener.onAgentDisconnect(roomId, liveChatMessage);
                        } else {
                            subscriptionListener.onMessage(roomId, liveChatMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case STREAM_LIVECHAT_ROOM:
                if (agentConnectListener != null) {
                    AgentObject agent = new AgentObject(array.optJSONObject(0).optJSONObject("data"));
                    agentConnectListener.onAgentConnect(agent);
                }
                break;
            case NOTIFY_ROOM:
                if (typingListener != null) {
                    String roomId = object.optJSONObject("fields").optString("eventName");
                    String user = array.optString(0);
                    Boolean typing = array.optBoolean(1);
                    typingListener.onTyping(roomId, user, typing);
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

    private JsonAdapter<LiveChatMessage> messageAdapter;

    private JsonAdapter<LiveChatMessage> getMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = moshi.adapter(LiveChatMessage.class);
        }
        return messageAdapter;
    }

    public enum SubType {
        STREAM_ROOM_MESSAGES,
        STREAM_LIVECHAT_ROOM,
        NOTIFY_ROOM
    }

}
