package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatTypingRPC extends RPC{


    private static String NOTIFYROOM="stream-notify-room";
    /**
     * Username and User ID are both different
     * It requires only username to be sent or it won't work
     * @param integer
     * @param room_id
     * @param username
     * @param istyping
     * @return
     */

    public static String streamNotifyRoom(int integer, String room_id, String username, Boolean istyping){

//        return "{\n" +
//                "    \"msg\": \"method\",\n" +
//                "    \"method\": \"stream-notify-room\",\n" +
//                "    \"id\": \""+integer+"\",\n" +
//                "    \"params\": [\n" +
//                "        \""+room_id+"/typing\",\n" +
//                "        \""+username+"\",\n" +
//                "        "+istyping+"\n" +
//                "    ]\n" +
//                "}";
        return getRemoteMethodObject(integer,NOTIFYROOM,room_id+"/typing",username,istyping).toString();
    }
}
