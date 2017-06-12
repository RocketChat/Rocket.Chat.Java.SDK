package io.rocketchat.livechat.middleware;

import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.LiveChatConfigObject;
import io.rocketchat.livechat.model.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 8/6/17.
 */

public class LiveChatMiddleware {


    //It will contain ConcurrentArrayList of all callback
    //Each new response will trigger each of the callback

    public enum AgentCallbackType{
        GETAGENTDATA,
        STREAMLIVECHATROOM
    }

    public enum CallbackType{
        GETINITIALDATA,
        REGISTER,
        LOGIN,
        GETCHATHISTORY,
        GETAGENTDATA
    }

    ConcurrentHashMap<Long,Object[]> callbacks;

    private static LiveChatMiddleware middleware= new LiveChatMiddleware();

    private LiveChatMiddleware(){
        callbacks= new ConcurrentHashMap<>();
    }

    public static LiveChatMiddleware getInstance(){
        return middleware;
    }

    public void createCallback(long i,Callback callback, CallbackType type){
        callbacks.put(i,new Object[]{callback,type});
    }

    public void processCallback(long i, JSONObject object){
        if (callbacks.containsKey(i)){
            Object[] objects=callbacks.remove(i);
            Callback callback= (Callback) objects[0];
            CallbackType type= (CallbackType) objects[1];
            switch (type) {
                case GETINITIALDATA:
                    InitialDataCallback dataCallback= (InitialDataCallback) callback;
                    LiveChatConfigObject liveChatConfigObject=new LiveChatConfigObject(object.optJSONObject("result"));
                    dataCallback.call(liveChatConfigObject);
                    break;
                case REGISTER: {
                    GuestCallback guestCallback = (GuestCallback) callback;
                    GuestObject guestObject = new GuestObject(object.optJSONObject("result"));
                    guestCallback.call(CallbackType.REGISTER, guestObject);
                }
                    break;
                case LOGIN:
                    GuestCallback guestCallback= (GuestCallback) callback;
                    GuestObject guestObject=new GuestObject(object.optJSONObject("result"));
                    guestCallback.call(CallbackType.LOGIN,guestObject);
                    break;
                case GETCHATHISTORY:
                    ArrayList <MessageObject> list=new ArrayList<MessageObject>();
                    HistoryCallback historymessages = (HistoryCallback) callback;
                    JSONArray array=object.optJSONObject("result").optJSONArray("messages");
                    for (int j=0;j<array.length();j++){
                        list.add(new MessageObject(array.optJSONObject(j)));
                    }
                    int unreadNotLoaded=object.optJSONObject("result").optInt("unreadNotLoaded");
                    historymessages.call(list,unreadNotLoaded);
                    break;
                case GETAGENTDATA:
                    AgentCallback agentCallback= (AgentCallback) callback;
                    AgentObject agentObject=new AgentObject(object.optJSONObject("result"));
                    agentCallback.call(AgentCallbackType.GETAGENTDATA,agentObject);
                    break;
            }

        }

    }
}
