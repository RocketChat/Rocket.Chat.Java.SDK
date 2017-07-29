package io.rocketchat.common.data.rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 13/6/17.
 */

public class RPC {

    public enum MsgType{
        PING,
        CONNECTED,
        ADDED,
        RESULT,
        READY,
        CHANGED,
        NOSUB,
        OTHER
    }

    //Currently Used
    public static String TYPE_PING="ping";
    public static String TYPE_CONNECTED="connected";
    public static String TYPE_ADDED="added";
    public static String TYPE_RESULT="result";
    public static String TYPE_READY="ready";
    public static String TYPE_CHANGED="changed";
    public static String TYPE_NOSUB = "nosub";


    //Maybe required in future
    public static String TYPE_UNSUB="unsub";
    public static String TYPE_SUB="sub";
    public static final String TYPE_REMOVED = "removed";
    public static final String TYPE_PONG = "pong";
    public static final String TYPE_UPDATED = "updated";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_CLOSED = "closed";


    public static MsgType parse(String s){
        if (s.equals(TYPE_PING)){
            return MsgType.PING;
        }else if (s.equals(TYPE_CONNECTED)){
            return MsgType.CONNECTED;
        }else if (s.equals(TYPE_ADDED)){
            return MsgType.ADDED;
        }else if (s.equals(TYPE_RESULT)){
            return MsgType.RESULT;
        }else if (s.equals(TYPE_READY)){
            return MsgType.READY;
        }else if (s.equals(TYPE_CHANGED)){
            return MsgType.CHANGED;
        }else if (s.equals(TYPE_NOSUB)){
            return MsgType.NOSUB;
        }else {
            return MsgType.OTHER;
        }
    }

    /**
     * Tested
     * @return
     */

    public static String ConnectObject(){
        return "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}";
    }

    public static JSONObject getRemoteMethodObject(int integer,String methodName, Object ... args){
        JSONObject object=new JSONObject();
        try {
            object.put("msg","method");
            object.put("method",methodName);
            object.put("id",String.valueOf(integer));
            JSONArray params=new JSONArray();
            for (int i=0;i<args.length;i++){
                params.put(args[i]);
            }
            object.put("params",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


}
