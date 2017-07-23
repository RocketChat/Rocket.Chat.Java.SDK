package io.rocketchat.core.middleware;

import io.rocketchat.core.callback.SubscribeListener;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 21/7/17.
 */

public class CoreStreamMiddleware {


    public enum SubType {
        SUBSCRIBEROOM,
        OTHER
    }

    public static CoreStreamMiddleware middleware=new CoreStreamMiddleware();

    ConcurrentHashMap<String,Object[]> subcallbacks;

    private CoreStreamMiddleware(){
        subcallbacks=new ConcurrentHashMap<>();
    }

    public static CoreStreamMiddleware getInstance(){
        return middleware;
    }

    public void createSubCallback(String id, SubscribeListener callback, SubType subscription){
        subcallbacks.put(id,new Object[]{callback,subscription});
    }

    public void processCallback(JSONObject object){
        String s = object.optString("collection");

    }

    public static SubType parse(String s){
        if (s.equals("stream-room-messages")) {
            return SubType.SUBSCRIBEROOM;
        }
        return SubType.OTHER;
    }
}
