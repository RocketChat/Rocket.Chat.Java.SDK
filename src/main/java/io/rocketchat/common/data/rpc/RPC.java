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
        OTHER
    }

    public static String TYPE_PING="ping";
    public static String TYPE_CONNECTED="connected";
    public static String TYPE_ADDED="added";
    public static String TYPE_RESULT="result";
    public static String TYPE_READY="ready";
    public static String TYPE_CHANGED="changed";


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
        }else {
            return MsgType.OTHER;
        }
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
