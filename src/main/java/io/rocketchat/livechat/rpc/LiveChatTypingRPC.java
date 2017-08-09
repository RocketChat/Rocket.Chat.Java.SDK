package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatTypingRPC extends RPC {

    private static final String NOTIFY_ROOM = "stream-notify-room";

    /**
     * TESTED
     * Username and User ID are both different
     * It requires only username to be sent or it won't work
     *
     * @param integer
     * @param room_id
     * @param username
     * @param istyping
     * @return
     */

    public static String streamNotifyRoom(int integer, String room_id, String username, Boolean istyping) {

        return getRemoteMethodObject(integer, NOTIFY_ROOM, room_id + "/typing", username, istyping).toString();
    }
}
