package com.rocketchat.livechat.internal.middleware;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.utils.Pair;
import com.rocketchat.common.utils.Types;
import com.rocketchat.livechat.callback.AgentCallback;
import com.rocketchat.livechat.callback.AuthCallback;
import com.rocketchat.livechat.callback.InitialDataCallback;
import com.rocketchat.livechat.callback.LoadHistoryCallback;
import com.rocketchat.livechat.callback.MessageListener;
import com.rocketchat.livechat.model.AgentObject;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatConfigObject;
import com.rocketchat.livechat.model.LiveChatMessage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 20/8/17 process callbacks in background or UI threads
public class LiveChatMiddleware {


    private final Moshi moshi;
    private ConcurrentHashMap<Long, Pair<? extends Callback, CallbackType>> callbacks;

    public LiveChatMiddleware(Moshi moshi) {
        this.moshi = moshi;
        callbacks = new ConcurrentHashMap<>();
    }

    public void createCallback(long i, Callback callback, CallbackType type) {
        callbacks.put(i, Pair.create(callback, type));
    }

    public void processCallback(long i, JSONObject object) {
        if (callbacks.containsKey(i)) {
            Pair<? extends Callback, CallbackType> callbackPair = callbacks.remove(i);
            Callback callback = callbackPair.first;
            CallbackType type = callbackPair.second;
            Object result = object.opt("result");

            /*
             * Possibly add a validateResponse(result, type) here or return some
             * RocketChatInvalidResponseException...
             */
            if (result == null) {
                JSONObject error = object.optJSONObject("error");
                if (error == null) {
                    String message = "Missing \"result\" or \"error\" values: " + object.toString();
                    callback.onError(new RocketChatInvalidResponseException(message));
                } else {
                    callback.onError(new RocketChatApiException(object.optJSONObject("error")));
                }
                return;
            }

            try {
                switch (type) {
                    case GET_INITIAL_DATA:
                        InitialDataCallback dataCallback = (InitialDataCallback) callback;
                        LiveChatConfigObject liveChatConfigObject = new LiveChatConfigObject((JSONObject) result);
                        dataCallback.onInitialData(liveChatConfigObject);
                        break;
                    case REGISTER:
                        AuthCallback.RegisterCallback registerCallback = (AuthCallback.RegisterCallback) callback;
                        GuestObject guestObject = new GuestObject((JSONObject) result);
                        registerCallback.onRegister(guestObject);
                        break;
                    case LOGIN:
                        AuthCallback.LoginCallback loginCallback = (AuthCallback.LoginCallback) callback;
                        guestObject = new GuestObject((JSONObject) result);
                        loginCallback.onLogin(guestObject);
                        break;
                    case GET_CHAT_HISTORY:
                        LoadHistoryCallback historyCallback = (LoadHistoryCallback) callback;
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        List<LiveChatMessage> list = getMessageListAdapter().fromJson(array.toString());
                        int unreadNotLoaded = object.optJSONObject("result").optInt("unreadNotLoaded");
                        historyCallback.onLoadHistory(list, unreadNotLoaded);
                        break;
                    case GET_AGENT_DATA:
                        AgentCallback.AgentDataCallback agentDataCallback = (AgentCallback.AgentDataCallback) callback;
                        AgentObject agentObject = new AgentObject((JSONObject) result);
                        agentDataCallback.onAgentData(agentObject);
                        break;
                    case SEND_MESSAGE:
                        MessageListener.MessageAckCallback messageAckCallback = (MessageListener.MessageAckCallback) callback;
                        LiveChatMessage liveChatMessage = getMessageAdapter().fromJson(result.toString());
                        messageAckCallback.onMessageAck(liveChatMessage);
                        break;
                    case SEND_OFFLINE_MESSAGE:
                        MessageListener.OfflineMessageCallback messageCallback = (MessageListener.OfflineMessageCallback) callback;
                        messageCallback.onOfflineMesssageSuccess((Boolean) result);
                        break;
                }
            } catch (IOException e) {
                callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                e.printStackTrace();
            }
        }
    }

    public void notifyDisconnection(String message) {
        RocketChatException error = new RocketChatNetworkErrorException(message);
        for (Map.Entry<Long, Pair<? extends Callback, CallbackType>> entry : callbacks.entrySet()) {
            entry.getValue().first.onError(error);
        }
        cleanup();
    }

    public void cleanup() {
        callbacks.clear();
    }

    private JsonAdapter<LiveChatMessage> messageAdapter;
    private JsonAdapter<List<LiveChatMessage>> messageListAdapter;

    private JsonAdapter<LiveChatMessage> getMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = moshi.adapter(LiveChatMessage.class);
        }
        return messageAdapter;
    }

    private JsonAdapter<List<LiveChatMessage>> getMessageListAdapter() {
        if (messageListAdapter == null) {
            Type type = Types.newParameterizedType(List.class, LiveChatMessage.class);
            messageListAdapter = moshi.adapter(type);
        }
        return messageListAdapter;
    }

    public enum CallbackType {
        GET_INITIAL_DATA,
        REGISTER,
        LOGIN,
        GET_CHAT_HISTORY,
        GET_AGENT_DATA,
        SEND_MESSAGE,
        SEND_OFFLINE_MESSAGE
    }
}
