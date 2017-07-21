package io.rocketchat.livechat.callback.adapter;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.LiveChatConfigObject;
import io.rocketchat.livechat.model.LiveChatMessage;

import java.util.ArrayList;

/**
 * Created by sachin on 21/7/17.
 */
public class LiveChatAdpater implements AgentListener.AgentConnectListener,
        AgentListener.AgentDataListener,
        AuthListener.RegisterListener,
        AuthListener.LoginListener,
        InitialDataListener,
        LoadHistoryListener,
        MessageListener.MessageAckListener,
        MessageListener.OfflineMessageListener,
        MessageListener.SubscriptionListener,
        SubscribeListener,
        TypingListener
{
    @Override
    public void onAgentConnect(AgentObject agentObject) {

    }

    @Override
    public void onAgentData(AgentObject agentObject, ErrorObject error) {

    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {

    }

    @Override
    public void onRegister(GuestObject object, ErrorObject error) {

    }

    @Override
    public void onInitialData(LiveChatConfigObject object, ErrorObject error) {

    }

    @Override
    public void onLoadHistory(ArrayList<LiveChatMessage> list, int unreadNotLoaded, ErrorObject error) {

    }

    @Override
    public void onMessageAck(LiveChatMessage object, ErrorObject error) {

    }

    @Override
    public void onOfflineMesssageSuccess(Boolean success, ErrorObject error) {

    }

    @Override
    public void onMessage(String roomId, LiveChatMessage object) {

    }

    @Override
    public void onAgentDisconnect(String roomId, LiveChatMessage object) {

    }

    @Override
    public void onSubscribe(LiveChatStreamMiddleware.SubType type, String subId) {

    }

    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {

    }
}
