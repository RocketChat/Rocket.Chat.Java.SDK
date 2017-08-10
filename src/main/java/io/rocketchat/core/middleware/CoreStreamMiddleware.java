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


    public void createSub(String roomId, Listener listener) {
        if (listener != null){
            subs.put(roomId, listener);
        }
    }

    public void removeSub(String roomId){
        subs.remove(roomId);
    }

    public void createSubCallback(String subId, SubscribeListener callback) {
        if (callback != null) {
            subcallbacks.put(subId, callback);
        }
    }

    public void processCallback(JSONObject object) {
        String s = object.optString("collection");
        JSONArray array = object.optJSONObject("fields").optJSONArray("args");
        String roomId = object.optJSONObject("fields").optString("eventName").replace("/typing","");

        if (subs.containsKey(roomId)) {
            Listener listener = subs.get(roomId);

            System.out.println("Type is "+listener.getClass());

            switch (parse(s)) {
                case SUBSCRIBE_ROOM_MESSAGE:
                    MessageListener.SubscriptionListener subscriptionListener = (MessageListener.SubscriptionListener) listener;
                    RocketChatMessage message = new RocketChatMessage(array.optJSONObject(0));
                    subscriptionListener.onMessage(roomId, message);
                    break;
                case SUBSCRIBE_ROOM_TYPING:
                    TypingListener typingListener = (TypingListener) listener;
                    typingListener.onTyping(roomId, array.optString(0), array.optBoolean(1));
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
