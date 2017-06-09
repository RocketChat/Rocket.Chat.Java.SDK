package io.rocketchat.livechat.rpc;

/**
 * Created by sachin on 9/6/17.
 */
public class LiveChatSubRPC {

    public static String streamRoomMessages(int uniqueid, String room_id){
        return "{\n" +
                "    \"msg\": \"sub\",\n" +
                "    \"id\": \""+uniqueid+"\",\n" +
                "    \"name\": \"stream-room-messages\",\n" +
                "    \"params\":[\n" +
                "        \""+room_id+"\",\n" +
                "        true\n" +
                "    ]\n" +
                "}";
    }

    public static String streamLivechatRoom(int uniqueid, String room_id){
        return "{\n" +
                "    \"msg\": \"sub\",\n" +
                "    \"id\": \""+uniqueid+"\",\n" +
                "    \"name\": \"stream-livechat-room\",\n" +
                "    \"params\":[\n" +
                "        \""+room_id+"\",\n" +
                "        true\n" +
                "    ]\n" +
                "}";
    }
}
