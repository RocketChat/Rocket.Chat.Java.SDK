package io.rocketchat.livechat.rpc;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatSubRPC {

    /**
     *
     * @param uniqueid
     * @param room_id
     * @param persistenceEnable Used for adding to collections, more like using sessions for maintaining subscriptions
     * @return
     */

    public static String streamRoomMessages(String uniqueid, String room_id,Boolean persistenceEnable){
        return "{\n" +
                "    \"msg\": \"sub\",\n" +
                "    \"id\": \""+uniqueid+"\",\n" +
                "    \"name\": \"stream-room-messages\",\n" +
                "    \"params\":[\n" +
                "        \""+room_id+"\",\n" +
                "        "+persistenceEnable+"\n" +
                "    ]\n" +
                "}";
    }

    public static String streamLivechatRoom(String uniqueid, String room_id, Boolean persistenceEnable){
        return "{\n" +
                "    \"msg\": \"sub\",\n" +
                "    \"id\": \""+uniqueid+"\",\n" +
                "    \"name\": \"stream-livechat-room\",\n" +
                "    \"params\":[\n" +
                "        \""+room_id+"\",\n" +
                "        "+persistenceEnable+"\n" +
                "    ]\n" +
                "}";
    }

    public static String subscribeTyping(String uniqueid, String room_id, Boolean persistenceEnable){
        return "{\"msg\":\"sub\"," +
                "\"id\":\""+uniqueid+"\"," +
                "\"name\":\"stream-notify-room\"," +
                "\"params\":[\""+room_id+"/typing\"," +
                ""+persistenceEnable+"]" +
                "}";
    }

}
