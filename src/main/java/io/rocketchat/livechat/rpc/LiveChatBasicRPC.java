package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 8/6/17.
 */

public class LiveChatBasicRPC extends RPC{

    public static String visitorToken =Utils.generateRandomHexToken(16);

    private static String GETINITIALDATA="livechat:getInitialData";
    private static String REGISTERGUEST="livechat:registerGuest";
    private static String LOGIN="login";
    private static String GETAGENTDATA="livechat:getAgentData";
    private static String CLOSECONVERSATION="livechat:closeByVisitor";
    private static String SENDOFFLINEMESSAGE="livechat:sendOfflineMessage";

    /**
     * Tested
     * @param integer
     * @return
     */

    public static String getInitialData(int integer){
        return getRemoteMethodObject(integer,GETINITIALDATA,visitorToken).toString();
    }

    /**
     * Tested
     * @param integer
     * @param name
     * @param email
     * @param dept
     * @return
     */

    public static String registerGuest(int integer,String name, String email, String dept){

            JSONObject object=new JSONObject();
            try {
                object.put("token",visitorToken);
                object.put("name",name);
                object.put("email",email);
                object.put("department",dept);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return getRemoteMethodObject(integer,REGISTERGUEST,object).toString();
    }

    /**
     * Tested
     * @param integer
     * @param token
     * @return
     */
    public static String login(int integer,String token){
            JSONObject object=new JSONObject();
            try {
                object.put("resume",token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return getRemoteMethodObject(integer,LOGIN,object).toString();

    }

    /**
     * Tested
     * @param integer
     * @param roomId
     * @return
     */
    public static String getAgentData(int integer, String roomId){
        return getRemoteMethodObject(integer,GETAGENTDATA,roomId).toString();
    }

    /**
     * Tested
     * @param integer
     * @param roomId
     * @return
     */

    public static String closeConversation(int integer,String roomId){
        return getRemoteMethodObject(integer,CLOSECONVERSATION,roomId).toString();
    }

    public static String sendOfflineMessage(int integer,String name, String email, String message){
        JSONObject object=new JSONObject();
        try {
            object.put("name",name);
            object.put("email",email);
            object.put("message",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer,SENDOFFLINEMESSAGE,object).toString();
    }

}
