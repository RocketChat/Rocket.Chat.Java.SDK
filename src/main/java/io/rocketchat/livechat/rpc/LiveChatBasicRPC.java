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

    public static String ConnectObject(){
        return "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}";
    }

    public static String getInitialData(int integer){

//        return "{\"msg\":\"method\"," +
//                "\"method\":\"livechat:getInitialData\"," +
//                "\"params\":[\""+ visitorToken +"\"]," +
//                "\"id\":\""+integer+"\"}";

        return getRemoteMethodObject(integer,GETINITIALDATA,visitorToken).toString();
//        return "{\"msg\":\"method\",\"method\":\"livechat:getInitialData\",\"params\":[\"7T4jzes7rX3Fr6cQ2\"],\"id\":\"1\"}";
    }

    public static String registerGuest(int integer,String name, String email, String dept){
//        return "{\"msg\":\"method\"," +
//                "\"method\":\"livechat:registerGuest\"," +
//                "\"params\":[{\"token\":\""+ visitorToken +"\",\"name\":\""+name+"\",\"email\":\""+email+"\",\"department\":\""+dept+"\"}]," +
//                "\"id\":\""+integer+"\"}";
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

    public static String login(int integer,String token){
//        return "{\n" +
//                "    \"msg\": \"method\",\n" +
//                "    \"method\": \"login\",\n" +
//                "    \"id\": \""+integer+"\",\n" +
//                "    \"params\":[\n" +
//                "        { \"resume\": \""+token+"\" }\n" +
//                "    ]\n" +
//                "}";
            JSONObject object=new JSONObject();
            try {
                object.put("resume",token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return getRemoteMethodObject(integer,LOGIN,object).toString();

    }

    public static String getAgentData(int integer, String roomId){
//        return "{\"msg\":\"method\"," +
//                "\"method\":\"livechat:getAgentData\"," +
//                "\"params\":[\""+roomId+"\"]," +
//                "\"id\":\""+integer+"\"}";
        return getRemoteMethodObject(integer,GETAGENTDATA,roomId).toString();
    }

    public static String closeConversation(int integer,String roomId){
//        return "{\"msg\":\"method\"," +
//                "\"method\":\"livechat:closeByVisitor\"," +
//                "\"params\":[\""+roomId+"\"]," +
//                "\"id\":\""+integer+"\"}";
        return getRemoteMethodObject(integer,CLOSECONVERSATION,roomId).toString();
    }

}
