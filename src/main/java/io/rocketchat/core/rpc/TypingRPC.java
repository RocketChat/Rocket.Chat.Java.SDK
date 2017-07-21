package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */
public class TypingRPC extends RPC {

    public static String SENDTYPING="stream-notify-room";

    public static String sendTyping(int integer, String room_id, String username,Boolean istyping){

        return getRemoteMethodObject(integer,SENDTYPING,room_id+"/typing",username,istyping).toString();
    }
}
