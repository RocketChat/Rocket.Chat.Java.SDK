package io.rocketchat.common.data.rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 13/6/17.
 */
public class SubRPC {

    public static JSONObject getRemoteSubscriptionObject(String uniqueId, String methodname, Object ... args){
        JSONObject object=new JSONObject();
        try {
            object.put("msg","sub");
            object.put("name",methodname);
            object.put("id",uniqueId);
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
