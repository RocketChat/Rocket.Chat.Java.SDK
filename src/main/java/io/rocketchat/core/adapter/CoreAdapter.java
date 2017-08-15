package io.rocketchat.core.adapter;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.core.callback.*;
import io.rocketchat.core.model.*;

import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreAdapter implements ConnectListener,
        HistoryListener,
        LoginListener,
        AccountListener.getPermissionsListener,
        AccountListener.getPublicSettingsListener,
        RoomListener.GetRoomListener,
        RoomListener.RoomRolesListener,
        RoomListener.GetMembersListener,
        EmojiListener,
        GetSubscriptionListener,
        UserListener.getUserRoleListener,
        MessageListener.MessageAckListener,
        MessageListener.SubscriptionListener,
        TypingListener {
    @Override
    public void onLoadHistory(List<RocketChatMessage> list, int unreadNotLoaded, ErrorObject error) {

    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {

    }

    @Override
    public void onGetRooms(List<RoomObject> rooms, ErrorObject error) {

    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {

    }

    @Override
    public void onUserRoles(List<UserObject> users, ErrorObject error) {

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
    public void onMessage(String roomId, RocketChatMessage message) {

    }

    @Override
    public void onGetPermissions(List<Permission> permissions, ErrorObject error) {

    }

    @Override
    public void onGetPublicSettings(List<PublicSetting> settings, ErrorObject error) {

    }

    @Override
    public void onGetRoomRoles(List<RoomRole> roles, ErrorObject error) {

    }

    @Override
    public void onListCustomEmoji(List<Emoji> emojis, ErrorObject error) {

    }

    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {

    }

    @Override
    public void onGetRoomMembers(Integer total, List<UserObject> members, ErrorObject error) {

    }
}
