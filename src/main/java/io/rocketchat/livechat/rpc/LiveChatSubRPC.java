package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.SubRPC;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatSubRPC extends SubRPC {

    private static final String STREAM_ROOM = "stream-room-messages";
    private static final String STREAM_LIVECHAT_ROOM = "stream-livechat-room";
    private static final String NOTIFY_ROOM = "stream-notify-room";

    /**
     * TESTED
     *
     * @param persistenceEnable Used for adding to collections, more like using sessions for maintaining subscriptions
     */

    public static String streamRoomMessages(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_ROOM, room_id, persistenceEnable).toString();
    }

    public static String streamLivechatRoom(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, STREAM_LIVECHAT_ROOM, room_id, persistenceEnable).toString();
    }

    /**
     * TESTED
     */
    public static String subscribeTyping(String uniqueid, String room_id, Boolean persistenceEnable) {
        return getRemoteSubscriptionObject(uniqueid, NOTIFY_ROOM, room_id + "/typing", persistenceEnable).toString();
    }

}
