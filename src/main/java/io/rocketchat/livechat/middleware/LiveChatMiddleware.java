package io.rocketchat.livechat.middleware;

import io.rocketchat.livechat.callbacks.Callback;
import io.rocketchat.livechat.callbacks.GuestCallback;
import io.rocketchat.livechat.callbacks.InitialDataCallback;
import io.rocketchat.livechat.models.GuestObject;
import io.rocketchat.livechat.models.LiveChatConfigObject;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 8/6/17.
 */

public class LiveChatMiddleware {

    //It will contain ConcurrentArrayList of all callbacks
    //Each new response will trigger each of the callback

    ConcurrentHashMap<Long,Callback> callbacks;

    private static LiveChatMiddleware middleware= new LiveChatMiddleware();

    private LiveChatMiddleware(){
        callbacks=new ConcurrentHashMap<Long, Callback>();
    }

    public static LiveChatMiddleware getInstance(){
        return middleware;
    }

    public void createCallback(long i,Callback callback){
        callbacks.put(i,callback);
    }

    public void processCallback(long i, JSONObject object){
        if (callbacks.containsKey(i)){
            Callback callback=callbacks.remove(i);
            if (callback instanceof InitialDataCallback){
                InitialDataCallback dataCallback= (InitialDataCallback) callback;
                LiveChatConfigObject liveChatConfigObject=new LiveChatConfigObject(object.optJSONObject("result"));
                dataCallback.call(liveChatConfigObject);
            }else if (callback instanceof GuestCallback){
                GuestCallback guestCallback= (GuestCallback) callback;
                GuestObject guestObject=new GuestObject(object.optJSONObject("result"));
                guestCallback.call(guestObject);
            }

        }

    }
}
