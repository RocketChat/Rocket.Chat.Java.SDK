package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */
public class TypingRPC extends RPC {

    private static String SEND_TYPING = "stream-notify-room";

    public static String sendTyping(int integer, String room_id, String username, Boolean istyping) {

        return getRemoteMethodObject(integer, SEND_TYPING, room_id + "/typing", username, istyping).toString();
    }
}
