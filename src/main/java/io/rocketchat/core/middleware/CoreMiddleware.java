package io.rocketchat.core.middleware;

import io.rocketchat.common.listener.Listener;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 18/7/17.
 */
public class CoreMiddleware {


    public enum ListenerType {
        LOGIN,

    }

    ConcurrentHashMap<Long,Object[]> callbacks;

    public static CoreMiddleware middleware=new CoreMiddleware();

    private CoreMiddleware(){
        callbacks= new ConcurrentHashMap<>();
    }

    public static CoreMiddleware getInstance(){
        return middleware;
    }

    public void createCallback(long i, Listener listener, CoreMiddleware.ListenerType type){
        callbacks.put(i,new Object[]{listener,type});
    }

    public void processCallback(long i, JSONObject object){
        if (callbacks.containsKey(i)) {
            Object[] objects = callbacks.remove(i);
            Listener listener = (Listener) objects[0];
            LiveChatMiddleware.ListenerType type = (LiveChatMiddleware.ListenerType) objects[1];
            Object result = object.opt("result");
        }
    }
}
