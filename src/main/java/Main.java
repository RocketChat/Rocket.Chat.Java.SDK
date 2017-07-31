import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.callback.HistoryListener;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.callback.MessageListener;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.TokenObject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Pack200;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener, LoginListener{


    RocketChatAPI api;
    RocketChatAPI.ChatRoom room;
    private static String serverurl="wss://demo.rocket.chat/websocket";
    private static String token="ju-c1BRuPmcUhKSFgLPoh9L6bhyEhHCrdMuX9NlKAe3";

    public void call(){
        api=new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);




    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken(token,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        if (error==null) {
            System.out.println("Logged in successfully, returned token "+ token.getAuthToken());

            //logging out after 2 seconds

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    logout();
                }
            },2000);

        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    private void logout() {
       api.logout(new SimpleListener() {
           @Override
           public void callback(Boolean success, ErrorObject error) {
               if (success){
                   System.out.println("Logged out successfully");
               }
           }
       });
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Exception websocketException) {

    }


}


/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

//Bugs
// TODO: 29/7/17 Room created with a roomName, deleted again and created with same name mess up everything
