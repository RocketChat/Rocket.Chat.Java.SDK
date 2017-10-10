package com.rocketchat.livechat.middleware;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.callback.AgentListener;
import com.rocketchat.livechat.callback.AuthListener;
import com.rocketchat.livechat.callback.InitialDataListener;
import com.rocketchat.livechat.callback.LoadHistoryListener;
import com.rocketchat.livechat.callback.MessageListener;
import com.rocketchat.livechat.model.AgentObject;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatConfigObject;
import com.rocketchat.livechat.model.LiveChatMessage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 20/8/17 process callbacks in background or UI threads
public class LiveChatMiddleware {


    private ConcurrentHashMap<Long, Object[]> callbacks;

    public LiveChatMiddleware() {
        callbacks = new ConcurrentHashMap<>();
    }

    public void createCallback(long i, Listener listener, ListenerType type) {
        callbacks.put(i, new Object[]{listener, type});
    }

    public void processCallback(long i, JSONObject object) {
        if (callbacks.containsKey(i)) {
            Object[] objects = callbacks.remove(i);
            Listener listener = (Listener) objects[0];
            ListenerType type = (ListenerType) objects[1];
            Object result = object.opt("result");
            switch (type) {
                case GET_INITIAL_DATA:
                    InitialDataListener dataListener = (InitialDataListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        dataListener.onInitialData(null, errorObject);
                    } else {
                        LiveChatConfigObject liveChatConfigObject = new LiveChatConfigObject((JSONObject) result);
                        dataListener.onInitialData(liveChatConfigObject, null);
                    }
                    break;
                case REGISTER: {
                    AuthListener.RegisterListener registerListener = (AuthListener.RegisterListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        registerListener.onRegister(null, errorObject);
                    } else {
                        GuestObject guestObject = new GuestObject((JSONObject) result);
                        registerListener.onRegister(guestObject, null);
                    }
                }
                break;
                case LOGIN:
                    AuthListener.LoginListener loginListener = (AuthListener.LoginListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        loginListener.onLogin(null, errorObject);
                    } else {
                        GuestObject guestObject = new GuestObject((JSONObject) result);
                        loginListener.onLogin(guestObject, null);
                    }
                    break;
                case GET_CHAT_HISTORY:
                    LoadHistoryListener historyListener = (LoadHistoryListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        historyListener.onLoadHistory(null, 0, errorObject);
                    } else {
                        ArrayList<LiveChatMessage> list = new ArrayList<LiveChatMessage>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new LiveChatMessage(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = object.optJSONObject("result").optInt("unreadNotLoaded");
                        historyListener.onLoadHistory(list, unreadNotLoaded, null);
                    }
                    break;
                case GET_AGENT_DATA:
                    AgentListener.AgentDataListener agentDataListener = (AgentListener.AgentDataListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        agentDataListener.onAgentData(null, errorObject);
                    } else {
                        AgentObject agentObject = new AgentObject((JSONObject) result);
                        agentDataListener.onAgentData(agentObject, null);
                    }
                    break;
                case SEND_MESSAGE:
                    MessageListener.MessageAckListener messageAckListener = (MessageListener.MessageAckListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        messageAckListener.onMessageAck(null, errorObject);
                    } else {
                        LiveChatMessage liveChatMessage = new LiveChatMessage((JSONObject) result);
                        messageAckListener.onMessageAck(liveChatMessage, null);
                    }
                    break;
                case SEND_OFFLINE_MESSAGE:
                    MessageListener.OfflineMessageListener messageListener = (MessageListener.OfflineMessageListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        messageListener.onOfflineMesssageSuccess(false, errorObject);
                    } else {
                        messageListener.onOfflineMesssageSuccess((Boolean) result, null);
                    }
                    break;
            }

        }

    }

    public enum ListenerType {
        GET_INITIAL_DATA,
        REGISTER,
        LOGIN,
        GET_CHAT_HISTORY,
        GET_AGENT_DATA,
        SEND_MESSAGE,
        SEND_OFFLINE_MESSAGE
    }
}
