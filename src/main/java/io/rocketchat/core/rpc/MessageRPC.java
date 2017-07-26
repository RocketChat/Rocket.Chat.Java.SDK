package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 21/7/17.
 */

public class MessageRPC extends RPC{

    public static String SENDMESSAGE="sendMessage";
    public static String DELETEMESSAGE="deleteMessage";
    public static String UPDATEMESSAGE="updateMessage";
    public static String PINMESSAGE="pinMessage";
    public static String UNPINMESSAGE="unpinMessage";
    public static String STARMESSAGE="starMessage";
    public static String SETREACTION="setReaction";

    public static String sendMessage(int integer, String msgId, String roomId, String message){
        JSONObject object=new JSONObject();
        try {
            object.put("_id",msgId);
            object.put("rid",roomId);
            object.put("msg",message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteMethodObject(integer,SENDMESSAGE,object).toString();
    }


    public static String deleteMessage(int integer, String msgId){
        JSONObject object=new JSONObject();
        try {
            object.put("_id",msgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer,DELETEMESSAGE,object).toString();
    }


    public static String updateMessage(int integer, String msgId, String roomId, String message){
        JSONObject object=new JSONObject();
        try {
            object.put("_id",msgId);
            object.put("msg",message);
            object.put("rid",roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteMethodObject(integer,UPDATEMESSAGE,object).toString();
    }


    public static String pinMessage(int integer, JSONObject message){
        return getRemoteMethodObject(integer,PINMESSAGE,message).toString();
    }


    public static String unpinMessage(int integer, JSONObject message){
        return getRemoteMethodObject(integer,UNPINMESSAGE,message).toString();
    }


    public static String starMessage(int integer, String msgId, String roomId, Boolean starred){
        JSONObject object=new JSONObject();
        try {
            object.put("_id",msgId);
            object.put("rid",roomId);
            object.put("starred",starred);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer,STARMESSAGE,object).toString();
    }


    public static String setReaction(int integer, String emojiId, String msgId){
        return getRemoteMethodObject(integer,SETREACTION,emojiId,msgId).toString();
    }
}
