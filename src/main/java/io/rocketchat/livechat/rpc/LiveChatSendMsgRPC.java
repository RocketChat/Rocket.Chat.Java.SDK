package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */
public class LiveChatSendMsgRPC extends RPC{

    public static String SENDMESSAGE="sendMessageLivechat";

    //Token is register guest visitorToken (visitorToken), not visitorToken for authentication
    public static String sendMessage(int integer, String msgId, String roomId, String message,String token){
//        return "{\n" +
//                "    \"msg\": \"method\",\n" +
//                "    \"method\": \"sendMessageLivechat\",\n" +
//                "    \"id\": \""+integer+"\",\n" +
//                "    \"params\": [\n" +
//                "        {\n" +
//                "            \"_id\": \""+msgId+"\",\n" +
//                "            \"rid\": \""+roomId+"\",\n" +
//                "            \"msg\": \""+message+"\",\n" +
//                "            \"visitorToken\": \""+token+"\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";
        JSONObject object=new JSONObject();
        try {
            object.put("_id",msgId);
            object.put("rid",roomId);
            object.put("msg",message);
            object.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteMethodObject(integer,SENDMESSAGE,object).toString();
    }
}
