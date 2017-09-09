package com.rocketchat.core.adapter;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.core.callback.*;
import com.rocketchat.core.callback.RoomCallback;
import com.rocketchat.core.model.Emoji;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.PublicSetting;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.RoomObject;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreAdapter implements ConnectListener,
        HistoryCallback,
        LoginCallback,
        AccountListener.getPermissionsListener,
        AccountListener.getPublicSettingsListener,
        RoomCallback.GetRoomCallback,
        RoomCallback.RoomRolesCallback,
        RoomCallback.GetMembersCallback,
        EmojiListener,
        GetSubscriptionListener,
        UserListener.getUserRoleListener,
        MessageCallback.MessageAckCallback,
        MessageCallback.SubscriptionCallback,
        TypingListener {
    @Override
    public void onLoadHistory(List<RocketChatMessage> list, int unreadNotLoaded, ApiError error) {

    }

    @Override
    public void onLoginSuccess(TokenObject token) {

    }

    @Override
    public void onLoginError(Error error) {

    }

    @Override
    public void onGetRooms(List<RoomObject> rooms, ApiError error) {

    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ApiError error) {

    }

    @Override
    public void onUserRoles(List<UserObject> users, ApiError error) {

    }

    @Override
    public void onConnect(String sessionID) {

    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Throwable websocketException) {

    }

    @Override
    public void onMessageAck(RocketChatMessage message, ApiError error) {

    }

    @Override
    public void onMessage(String roomId, RocketChatMessage message) {

    }

    @Override
    public void onGetPermissions(List<Permission> permissions, ApiError error) {

    }

    @Override
    public void onGetPublicSettings(List<PublicSetting> settings, ApiError error) {

    }

    @Override
    public void onGetRoomRoles(List<RoomRole> roles, ApiError error) {

    }

    @Override
    public void onListCustomEmoji(List<Emoji> emojis, ApiError error) {

    }

    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {

    }

    @Override
    public void onGetRoomMembers(Integer total, List<UserObject> members, ApiError error) {

    }
}
