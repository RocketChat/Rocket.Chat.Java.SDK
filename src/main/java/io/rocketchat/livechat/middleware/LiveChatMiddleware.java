package io.rocketchat.livechat.middleware;

import io.rocketchat.common.data.model.ErrorObject;
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
        GETAGENTDATA,
        SENDMESSAGE
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
            JSONObject result=object.optJSONObject("result");
            switch (type) {
                case GETINITIALDATA:
                    InitialDataListener dataListener= (InitialDataListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        dataListener.onInitialData(null,errorObject);
                    }else{
                        LiveChatConfigObject liveChatConfigObject=new LiveChatConfigObject(result);
                        dataListener.onInitialData(liveChatConfigObject,null);
                    }
                    break;
                case REGISTER: {
                    AuthListener.RegisterListener registerListener = (AuthListener.RegisterListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        registerListener.onRegister(null,errorObject);
                    }else {
                        GuestObject guestObject = new GuestObject(result);
                        registerListener.onRegister(guestObject,null);
                    }
                }
                    break;
                case LOGIN:
                    AuthListener.LoginListener loginListener= (AuthListener.LoginListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        loginListener.onLogin(null,errorObject);
                    }else {
                        GuestObject guestObject = new GuestObject(result);
                        loginListener.onLogin(guestObject,null);
                    }
                    break;
                case GETCHATHISTORY:
                    LoadHistoryListener historyListener = (LoadHistoryListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        historyListener.onLoadHistory(null, 0,errorObject);
                    }else {
                        ArrayList <MessageObject> list=new ArrayList<MessageObject>();
                        JSONArray array = result.optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new MessageObject(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = object.optJSONObject("result").optInt("unreadNotLoaded");
                        historyListener.onLoadHistory(list, unreadNotLoaded,null);
                    }
                    break;
                case GETAGENTDATA:
                    AgentListener.AgentDataListener agentDataListener = (AgentListener.AgentDataListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        agentDataListener.onAgentData(null,errorObject);
                    }else {
                        AgentObject agentObject = new AgentObject(result);
                        agentDataListener.onAgentData(agentObject,null);
                    }
                    break;
                case SENDMESSAGE:

                    break;
            }

        }

    }
}
