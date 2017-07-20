package io.rocketchat.core.middleware;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.callback.RoomListener;
import io.rocketchat.core.callback.SubscriptionListener;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;
import io.rocketchat.livechat.model.MessageObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 18/7/17.
 */

public class CoreMiddleware {


    public enum ListenerType {
        LOGIN,
        GETUSERROLES,
        GETSUBSCRIPTIONS,
        GETROOMS
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
            CoreMiddleware.ListenerType type = (CoreMiddleware.ListenerType) objects[1];
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    LoginListener loginListener= (LoginListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        loginListener.onLogin(null,errorObject);
                    }else{
                        TokenObject tokenObject=new TokenObject((JSONObject) result);
                        loginListener.onLogin(tokenObject,null);
                    }
                    break;
                case GETUSERROLES:
                    RoomListener.GetRoomListener getRoomListener= (RoomListener.GetRoomListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        getRoomListener.onGetRooms(null,errorObject);
                    }else{
                        ArrayList<RoomObject> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomObject(array.optJSONObject(j)));
                        }
                        getRoomListener.onGetRooms(list,null);
                    }
                    break;
                case GETSUBSCRIPTIONS:
                    SubscriptionListener.GetSubscriptionListener subscriptionListener= (SubscriptionListener.GetSubscriptionListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        subscriptionListener.onGetSubscriptions(null,errorObject);
                    }else{
                        ArrayList<SubscriptionObject> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new SubscriptionObject(array.optJSONObject(j)));
                        }
                        subscriptionListener.onGetSubscriptions(list,null);
                    }
                    break;
                case GETROOMS:

                    break;
            }
        }
    }
}
