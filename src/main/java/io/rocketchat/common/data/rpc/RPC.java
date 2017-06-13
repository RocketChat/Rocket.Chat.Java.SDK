package io.rocketchat.common.data.rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 13/6/17.
 */
public class RPC {

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
