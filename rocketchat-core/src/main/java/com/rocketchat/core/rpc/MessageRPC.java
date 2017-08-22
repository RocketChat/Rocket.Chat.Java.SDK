package com.rocketchat.core.rpc;

import com.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 21/7/17.
 */

public class MessageRPC extends RPC {

    private static final String SEND_MESSAGE = "sendMessage";
    private static final String DELETE_MESSAGE = "deleteMessage";
    private static final String UPDATE_MESSAGE = "updateMessage";
    private static final String PIN_MESSAGE = "pinMessage";
    private static final String UNPIN_MESSAGE = "unpinMessage";
    private static final String STAR_MESSAGE = "starMessage";
    private static final String SET_REACTION = "setReaction";
    private static final String SEARCH_MESSAGE = "messageSearch";
    private static final String SEND_FILE_MESSAGE = "sendFileMessage";

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

    public static String searchMessage(int integer, String message, String roomId, int limit) {
        return getRemoteMethodObject(integer, SEARCH_MESSAGE, message, roomId, limit).toString();
    }

    public static String sendFileMessage(int integer, String roomId, String store, String fileId, String fileType, int size, String fileName, String desc, String url) {

        JSONObject file = new JSONObject();
        try {
            file.put("_id", fileId);
            file.put("type", fileType);
            file.put("size", size);
            file.put("name", fileName);
            file.put("description", desc);
            file.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRemoteMethodObject(integer, SEND_FILE_MESSAGE, roomId, store, file).toString();
    }
}
