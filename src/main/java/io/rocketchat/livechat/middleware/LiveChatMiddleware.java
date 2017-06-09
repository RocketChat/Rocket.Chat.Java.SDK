package io.rocketchat.livechat.middleware;

import io.rocketchat.livechat.callbacks.*;
import io.rocketchat.livechat.models.AgentObject;
import io.rocketchat.livechat.models.GuestObject;
import io.rocketchat.livechat.models.LiveChatConfigObject;
import io.rocketchat.livechat.models.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
            }else if (callback instanceof MessagesCallback){
                ArrayList <MessageObject> list=new ArrayList<MessageObject>();
                MessagesCallback messagesCallback= (MessagesCallback) callback;
                JSONArray array=object.optJSONObject("result").optJSONArray("messages");
                for (int j=0;j<array.length();j++){
                    list.add(new MessageObject(array.optJSONObject(j)));
                }
                int unreadNotLoaded=object.optJSONObject("result").optInt("unreadNotLoaded");
                messagesCallback.call(list,unreadNotLoaded);
            }else if (callback instanceof AgentCallback){
                AgentCallback agentCallback= (AgentCallback) callback;
                AgentObject agentObject=new AgentObject(object.optJSONObject("result"));
                agentCallback.call(agentObject);
            }

        }

    }
}
