package com.rocketchat.core.internal.middleware;

import com.rocketchat.common.listener.Listener;
import com.rocketchat.common.listener.SubscribeCallback;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.utils.Types;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.model.Message;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 21/7/17.
 */

public class CoreStreamMiddleware {

    private final Moshi moshi;

    private ConcurrentHashMap<String, SubscribeCallback> listeners;
    private ConcurrentHashMap<String, ConcurrentHashMap<SubscriptionType, Listener>> subs;

    public CoreStreamMiddleware(Moshi moshi) {
        this.moshi = moshi;
        listeners = new ConcurrentHashMap<>();
        subs = new ConcurrentHashMap<>();
    }


    public void createSubscription(String roomId, Listener listener, SubscriptionType type) {
        if (listener != null) {
            if (subs.containsKey(roomId)) {
                subs.get(roomId).put(type, listener);
            } else {
                ConcurrentHashMap<SubscriptionType, Listener> map = new ConcurrentHashMap<>();
                map.put(type, listener);
                subs.put(roomId, map);
            }
        }
    }

    public void removeAllSubscriptions(String roomId) {
        subs.remove(roomId);
    }

    public void removeSubscription(String roomId, SubscriptionType type) {
        if (subs.containsKey(roomId)) {
            subs.get(roomId).remove(type);
        }
    }


    public void createSubscriptionListener(String subId, SubscribeCallback callback) {
        if (callback != null) {
            listeners.put(subId, callback);
        }
    }

    public void processListeners(JSONObject object) {
        CollectionType collectionType = getCollectionType(object);

        String collection = object.optString("collection");
        JSONArray array = object.optJSONObject("fields").optJSONArray("args");
        String roomId = object.optJSONObject("fields").optString("eventName").replace("/typing", "");
        Listener listener;

        if (subs.containsKey(roomId)) {
            switch (parse(collection)) {
                case SUBSCRIBE_ROOM_MESSAGE:
                    listener = subs.get(roomId).get(SubscriptionType.SUBSCRIBE_ROOM_MESSAGE);
                    MessageCallback.MessageListener messageListener = (MessageCallback.MessageListener) listener;

                    try {
                        Message message = getMessageAdapter().fromJson(array.getJSONObject(0).toString());
                        messageListener.onMessage(roomId, message);
                    } catch (IOException | JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                case SUBSCRIBE_ROOM_TYPING:
                    listener = subs.get(roomId).get(SubscriptionType.SUBSCRIBE_ROOM_TYPING);
                    TypingListener typingListener = (TypingListener) listener;
                    typingListener.onTyping(roomId, array.optString(0), array.optBoolean(1));
                    break;
                case OTHER:
                    break;
            }
        }
    }

    public void processSubscriptionSuccess(JSONObject subObj) {
        if (subObj.optJSONArray("subs") != null) {
            String id = subObj.optJSONArray("subs").optString(0);
            if (listeners.containsKey(id)) {
                listeners.remove(id).onSubscribe(true, id);
            }
        }
    }

    public void processUnsubscriptionSuccess(JSONObject unsubObj) {
        String id = unsubObj.optString("id");
        if (listeners.containsKey(id)) {
            SubscribeCallback subscribeCallback = listeners.remove(id);
            subscribeCallback.onSubscribe(false, id);
        }
    }

    private JsonAdapter<Message> messageAdapter;
    private JsonAdapter<List<Message>> messageListAdapter;

    private JsonAdapter<Message> getMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = moshi.adapter(Message.class);
        }
        return messageAdapter;
    }

    private JsonAdapter<List<Message>> getMessageListAdapter() {
        if (messageListAdapter == null) {
            Type objectType = Types.newParameterizedType(List.class, Message.class);
            messageListAdapter = moshi.adapter(objectType);
        }
        return messageListAdapter;
    }

    public void cleanup() {
        listeners.clear();
        for (ConcurrentHashMap<SubscriptionType, Listener> sub : subs.values()) {
            sub.clear();
        }
        subs.clear();
    }

    public enum SubscriptionType {
        SUBSCRIBE_ROOM_MESSAGE,
        SUBSCRIBE_ROOM_TYPING,
        OTHER
    }

    private static SubscriptionType parse(String s) {
        if (s.equals("stream-room-messages")) {
            return SubscriptionType.SUBSCRIBE_ROOM_MESSAGE;
        } else if (s.equals("stream-notify-room")) {
            return SubscriptionType.SUBSCRIBE_ROOM_TYPING;
        }
        return SubscriptionType.OTHER;
    }

    private static final String COLLECTION_TYPE_USERS = "users";
    private static final String COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF = "meteor_accounts_loginServiceConfiguration";
    private static final String COLLECTION_TYPE_ROCKETCHAT_ROLES = "rocketchat_roles";
    private static final String COLLECTION_TYPE_METEOR_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    public enum CollectionType {
        STREAM_COLLECTION,
        COLLECTION
    }

    private CollectionType getCollectionType(JSONObject object) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_USERS) ||
                collectionName.equals(COLLECTION_TYPE_METEOR_ACCOUNTS_LOGIN_CONF) ||
                collectionName.equals(COLLECTION_TYPE_METEOR_CLIENT_VERSIONS) ||
                collectionName.equals(COLLECTION_TYPE_ROCKETCHAT_ROLES)) {
            return CollectionType.COLLECTION;
        } else {
            return CollectionType.STREAM_COLLECTION;
        }
    }

}
