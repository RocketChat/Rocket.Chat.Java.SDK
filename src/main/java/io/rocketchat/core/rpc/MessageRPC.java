package io.rocketchat.core.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */

public class MessageRPC extends RPC {

    private static String SEND_MESSAGE = "sendMessage";
    private static String DELETE_MESSAGE = "deleteMessage";
    private static String UPDATE_MESSAGE = "updateMessage";
    private static String PIN_MESSAGE = "pinMessage";
    private static String UNPIN_MESSAGE = "unpinMessage";
    private static String STAR_MESSAGE = "starMessage";
    private static String SET_REACTION = "setReaction";

    public static String sendMessage(int integer, String msgId, String roomId, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", msgId);
            object.put("rid", roomId);
            object.put("msg", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteMethodObject(integer, SEND_MESSAGE, object).toString();
    }

    public static String deleteMessage(int integer, String msgId) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", msgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer, DELETE_MESSAGE, object).toString();
    }

    public static String updateMessage(int integer, String msgId, String roomId, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", msgId);
            object.put("msg", message);
            object.put("rid", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteMethodObject(integer, UPDATE_MESSAGE, object).toString();
    }

    public static String pinMessage(int integer, JSONObject message) {
        return getRemoteMethodObject(integer, PIN_MESSAGE, message).toString();
    }

    public static String unpinMessage(int integer, JSONObject message) {
        return getRemoteMethodObject(integer, UNPIN_MESSAGE, message).toString();
    }

    public static String starMessage(int integer, String msgId, String roomId, Boolean starred) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", msgId);
            object.put("rid", roomId);
            object.put("starred", starred);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer, STAR_MESSAGE, object).toString();
    }

    public static String setReaction(int integer, String emojiId, String msgId) {
        return getRemoteMethodObject(integer, SET_REACTION, emojiId, msgId).toString();
    }
}
