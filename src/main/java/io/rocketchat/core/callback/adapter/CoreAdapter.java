package io.rocketchat.core.callback.adapter;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.core.callback.*;
import io.rocketchat.core.middleware.CoreStreamMiddleware;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;

import java.util.ArrayList;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreAdapter implements ConnectListener,
        HistoryListener,
        LoginListener,
        RoomListener.GetRoomListener,
        SubscriptionListener.GetSubscriptionListener,
        UserListener.getUserRoleListener,
        MessageListener.MessageAckListener,
        MessageListener.SubscriptionListener,
        SubscribeListener{
    @Override
    public void onLoadHistory(ArrayList<RocketChatMessage> list, int unreadNotLoaded, ErrorObject error) {

    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {

    }

    @Override
    public void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error) {

    }

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {

    }

    @Override
    public void onUserRoles(ArrayList<UserObject> users, ErrorObject error) {

    }

    @Override
    public void onConnect(String sessionID) {

    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }

    @Override
    public void onMessageAck(RocketChatMessage message, ErrorObject error) {

    }

    @Override
    public void onSubscribe(CoreStreamMiddleware.SubType type, String subId) {

    }

    @Override
    public void onMessage(String roomId, RocketChatMessage message) {

    }
}
