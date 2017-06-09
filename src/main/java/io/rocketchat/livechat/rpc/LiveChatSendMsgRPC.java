package io.rocketchat.livechat.rpc;

/**
 * Created by sachin on 9/6/17.
 */
public class LiveChatSendMsgRPC {


    //Token is register guest token (visitorToken), not token for authentication
    public static String sendMessage(int integer, String msgId, String roomId, String message,String token){
        return "{\n" +
                "    \"msg\": \"method\",\n" +
                "    \"method\": \"sendMessageLivechat\",\n" +
                "    \"id\": \""+integer+"\",\n" +
                "    \"params\": [\n" +
                "        {\n" +
                "            \"_id\": \""+msgId+"\",\n" +
                "            \"rid\": \""+roomId+"\",\n" +
                "            \"msg\": \""+message+"\",\n" +
                "            \"token\": \""+token+"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }
}
