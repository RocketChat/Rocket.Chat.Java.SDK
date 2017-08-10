package io.rocketchat.core.middleware;

import io.rocketchat.common.listener.Listener;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.core.callback.MessageListener;
import io.rocketchat.core.model.RocketChatMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 21/7/17.
 */

public class CoreStreamMiddleware {


    private ConcurrentHashMap<String, SubscribeListener> subcallbacks;
    private ConcurrentHashMap<String, Listener> subs;

    public CoreStreamMiddleware() {
        subcallbacks = new ConcurrentHashMap<>();
        subs = new ConcurrentHashMap<>();
    }


    public void createSub(String id, Listener listener) {
        if (listener != null){
            subs.put(id, listener);
        }
    }

    public void createSubCallback(String id, SubscribeListener callback) {
        if (callback != null) {
            subcallbacks.put(id, callback);
        }
    }

    public void processCallback(JSONObject object) {
        String s = object.optString("collection");
        String id = object.optString("id");
        System.out.println("id is "+id);
        JSONArray array = object.optJSONObject("fields").optJSONArray("args");

        if (subs.containsKey(id)) {
            Listener listener = subs.remove(id);

            switch (parse(s)) {
                case SUBSCRIBE_ROOM_MESSAGE:
                    MessageListener.SubscriptionListener subscriptionListener = (MessageListener.SubscriptionListener) listener;
                    RocketChatMessage message = new RocketChatMessage(array.optJSONObject(0));
                    String roomId = object.optJSONObject("fields").optString("eventName");
                    subscriptionListener.onMessage(roomId, message);
                    break;
                case SUBSCRIBE_ROOM_TYPING:
                    TypingListener typingListener = (TypingListener) listener;
                    typingListener.onTyping(object.optJSONObject("fields").optString("eventName"), array.optString(0), array.optBoolean(1));
                    break;
                case OTHER:
                    break;
            }
        }

    }

    public void processSubSuccess(JSONObject subObj) {
        if (subObj.optJSONArray("subs") != null) {
            String id = subObj.optJSONArray("subs").optString(0);
            if (subcallbacks.containsKey(id)) {
                subcallbacks.remove(id).onSubscribe(true, id);
            }
        }
    }

    public void processUnsubSuccess(JSONObject unsubObj) {
        String id = unsubObj.optString("id");
        if (subcallbacks.containsKey(id)) {
            SubscribeListener subscribeListener = subcallbacks.remove(id);
            subscribeListener.onSubscribe(false, id);
        }
    }

    public enum SubType {
        SUBSCRIBE_ROOM_MESSAGE,
        SUBSCRIBE_ROOM_TYPING,
        OTHER
    }

    private static SubType parse(String s) {
        if (s.equals("stream-room-messages")) {
            return SubType.SUBSCRIBE_ROOM_MESSAGE;
        } else if (s.equals("stream-notify-room")) {
            return SubType.SUBSCRIBE_ROOM_TYPING;
        }
        return SubType.OTHER;
    }

}
