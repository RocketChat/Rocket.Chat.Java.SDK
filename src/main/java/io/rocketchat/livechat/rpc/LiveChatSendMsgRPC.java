package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatSendMsgRPC extends RPC{

    public static String SENDMESSAGE="sendMessageLivechat";

    /**
     * TESTED
     * @param integer
     * @param msgId
     * @param roomId
     * @param message
     * @param token Token is register guest visitorToken (visitorToken), not visitorToken for authentication
     * @return
     */
    public static String sendMessage(int integer, String msgId, String roomId, String message,String token){
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
