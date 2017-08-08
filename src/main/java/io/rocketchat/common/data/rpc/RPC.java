package io.rocketchat.common.data.rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 13/6/17.
 */

public class RPC {

    public static final String TYPE_REMOVED = "removed";
    public static final String TYPE_UPDATED = "updated";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_CLOSED = "closed";
    //Currently Used
    private static String TYPE_PING = "ping";
    private static String TYPE_CONNECTED = "connected";
    private static String TYPE_ADDED = "added";
    private static String TYPE_RESULT = "result";
    private static String TYPE_READY = "ready";
    private static String TYPE_CHANGED = "changed";
    private static String TYPE_NOSUB = "nosub";
    private static String TYPE_PONG = "pong";
    //Maybe required in future
    public static String TYPE_UNSUB = "unsub";
    public static String TYPE_SUB = "sub";
    public static String PINGMESSAGE = "{\"msg\":\"ping\"}";
    public static String PONGMESSAGE = "{\"msg\":\"pong\"}";

    public static MsgType parse(String s) {
        if (s.equals(TYPE_PING)) {
            return MsgType.PING;
        } else if (s.equals(TYPE_CONNECTED)) {
            return MsgType.CONNECTED;
        } else if (s.equals(TYPE_ADDED)) {
            return MsgType.ADDED;
        } else if (s.equals(TYPE_RESULT)) {
            return MsgType.RESULT;
        } else if (s.equals(TYPE_READY)) {
            return MsgType.READY;
        } else if (s.equals(TYPE_CHANGED)) {
            return MsgType.CHANGED;
        } else if (s.equals(TYPE_NOSUB)) {
            return MsgType.NOSUB;
        } else if (s.equals(TYPE_PONG)) {
            return MsgType.PONG;
        } else {
            return MsgType.OTHER;
        }
    }

    /**
     * Tested
     *
     * @return
     */

    public static String ConnectObject() {
        return "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}";
    }

    public static JSONObject getRemoteMethodObject(int integer, String methodName, Object... args) {
        JSONObject object = new JSONObject();
        try {
            object.put("msg", "method");
            object.put("method", methodName);
            object.put("id", String.valueOf(integer));
            JSONArray params = new JSONArray();
            for (int i = 0; i < args.length; i++) {
                params.put(args[i]);
            }
            object.put("params", params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public enum MsgType {
        PING, PONG, CONNECTED, ADDED, RESULT, READY, CHANGED, NOSUB, OTHER
    }

}
