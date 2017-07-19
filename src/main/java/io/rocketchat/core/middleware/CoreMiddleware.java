package io.rocketchat.core.middleware;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.model.TokenObject;
import org.json.JSONObject;

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
                    break;
                case GETSUBSCRIPTIONS:
                    break;
                case GETROOMS:
                    break;
            }
        }
    }
}
