package io.rocketchat.livechat.rpc;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatTypingRPC {

    //Username and User ID are both different

    public static String streamNotifyRoom(int integer, String room_id, String username, Boolean istyping){

        return "{\n" +
                "    \"msg\": \"method\",\n" +
                "    \"method\": \"stream-notify-room\",\n" +
                "    \"id\": \""+integer+"\",\n" +
                "    \"params\": [\n" +
                "        \""+room_id+"/typing\",\n" +
                "        \""+username+"\",\n" +
                "        "+istyping+"\n" +
                "    ]\n" +
                "}";
    }
}
