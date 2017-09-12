package com.rocketchat.core.rpc;

import com.rocketchat.common.data.rpc.SubRPC;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreSubRPC extends SubRPC {
    // name (comes under collections)
    private static final String STREAM_ROOM_MESSAGES = "stream-room-messages";
    private static final String STREAM_NOTIFY_ROOM = "stream-notify-room";
    private static final String USER_DATA = "userData";
    private static final String ACTIVE_USERS = "activeUsers";
    private static final String ROLES = "roles";
    private static final String LOGIN_SERVICE_CONFIGURATION = "meteor.loginServiceConfiguration";
    private static final String AUTO_UPDATE_CLIENT_VERSIONS = "meteor_autoupdate_clientVersions";

    // might be used in future (comes under collections)
    private static final String STREAM_NOTIFY_USER = "stream-notify-user";
    private static final String STREAM_NOTIFY_LOGGED = "stream-notify-logged";
    private static final String STREAM_NOTIFY_ALL = "stream-notify-all";

    // Events (comes under params)
    private static final String TYPING_EVENT = "/typing";
    private static final String DELETE_EVENT = "/deleteMessage";
    // might be used in future
    private static final String MESSAGE_EVENT = "/message";
    private static final String ROLES_CHANGE_EVENT = "roles-change";
    private static final String USERS_NAME_CHANGED_EVENT = "Users:NameChanged";
    private static final String UPDATE_CUSTOM_SOUND_EVENT = "updateCustomSound";
    private static final String DELETE_CUSTOM_SOUND_EVENT = "deleteCustomSound";
    private static final String UPDATE_EMOJI_CUSTOM_EVENT = "updateEmojiCustom";
    private static final String DELETE_EMOJI_CUSTOM = "deleteEmojiCustom";
    private static final String OTR_EVENT = "/otr";
    private static final String WEBRTC_EVENT = "/webrtc";
    private static final String NOTIFICATION_EVENT = "/notification";
    private static final String UPDATE_AVATAR_EVENT = "updateAvatar";
    private static final String PUBLIC_SETTINGS_CHANGED_EVENT = "public-settings-changed";
    private static final String ROOMS_CHANGED_EVENT = "/rooms-changed";
    private static final String SUBSCRIPTIONS_CHANGED_EVENT = "/subscriptions-changed";
    private static final String PERMISSIONS_CHANGED_EVENT = "permissions-changed";

    public static String subscribeRoomMessageEvent(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_ROOM_MESSAGES, room_id, persistenceEnable).toString();
    }

    public static String subscribeRoomTypingEvent(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_NOTIFY_ROOM, room_id + TYPING_EVENT, persistenceEnable).toString();
    }

    public static String subscribeUserData(String uniqueid) {
        return getRemoteSubscriptionObject(uniqueid, USER_DATA).toString();
    }

    public static String subscribeActiveUsers(String uniqueid) {
        return getRemoteSubscriptionObject(uniqueid, ACTIVE_USERS).toString();
    }

    public static String subscribeLoginServiceConfiguration(String uniqueid) {
        return getRemoteSubscriptionObject(uniqueid, LOGIN_SERVICE_CONFIGURATION).toString();
    }

    public static String subscribeClientVersions(String uniqueid) {
        return getRemoteSubscriptionObject(uniqueid, AUTO_UPDATE_CLIENT_VERSIONS).toString();
    }

    public static String subscribeUserRoles(String uniqueId) {
        return getRemoteSubscriptionObject(uniqueId, ROLES).toString();
    }

    public static String subscribeRoomMessageDeleteEvent(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_NOTIFY_ROOM, room_id + DELETE_EVENT, persistenceEnable).toString();
    }


    public static String unsubscribeRoom(String subId) {
        return getRemoteUnsubscriptionObject(subId).toString();
    }

}
