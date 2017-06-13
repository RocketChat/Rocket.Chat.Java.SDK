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

    public enum ListenerType {
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

    public void createCallback(long i, Listener listener, ListenerType type){
        callbacks.put(i,new Object[]{listener,type});
    }

    public void processCallback(long i, JSONObject object){
        if (callbacks.containsKey(i)){
            Object[] objects=callbacks.remove(i);
            Listener listener = (Listener) objects[0];
            ListenerType type= (ListenerType) objects[1];
            switch (type) {
                case GETINITIALDATA:
                    InitialDataListener dataListener= (InitialDataListener) listener;
                    LiveChatConfigObject liveChatConfigObject=new LiveChatConfigObject(object.optJSONObject("result"));
                    dataListener.onInitialData(liveChatConfigObject);
                    break;
                case REGISTER: {
                    AuthListener.RegisterListener registerListener = (AuthListener.RegisterListener) listener;
                    GuestObject guestObject = new GuestObject(object.optJSONObject("result"));
                    registerListener.onRegister(guestObject);
                }
                    break;
                case LOGIN:
                    AuthListener.LoginListener loginListener= (AuthListener.LoginListener) listener;
                    GuestObject guestObject=new GuestObject(object.optJSONObject("result"));
                    loginListener.onLogin(guestObject);
                    break;
                case GETCHATHISTORY:
                    ArrayList <MessageObject> list=new ArrayList<MessageObject>();
                    LoadHistoryListener historyListener = (LoadHistoryListener) listener;
                    JSONArray array=object.optJSONObject("result").optJSONArray("messages");
                    for (int j=0;j<array.length();j++){
                        list.add(new MessageObject(array.optJSONObject(j)));
                    }
                    int unreadNotLoaded=object.optJSONObject("result").optInt("unreadNotLoaded");
                    historyListener.onLoadHistory(list,unreadNotLoaded);
                    break;
                case GETAGENTDATA:
                    AgentListener.AgentDataListener agentDataListener = (AgentListener.AgentDataListener) listener;
                    AgentObject agentObject=new AgentObject(object.optJSONObject("result"));
                    agentDataListener.onAgentData(agentObject);
                    break;
            }

        }

    }
}
