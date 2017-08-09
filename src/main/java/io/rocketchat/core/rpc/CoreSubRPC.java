package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.SubRPC;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreSubRPC extends SubRPC {
    private static final String STREAM_ROOM_MESSAGES = "stream-room-messages";
    private static final String STREAM_NOTIFY_ROOM = "stream-notify-room";

    public static String subscribeRoomMessageEvent(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_ROOM_MESSAGES, room_id, persistenceEnable).toString();
    }

    public static String subscribeRoomTypingEvent(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_NOTIFY_ROOM, room_id + "/typing", persistenceEnable).toString();
    }

    public static String unsubscribeRoom(String subId) {
        return getRemoteUnsubscriptionObject(subId).toString();
    }

}
