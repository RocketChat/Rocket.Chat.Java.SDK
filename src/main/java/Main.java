import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.AuthListener;
import io.rocketchat.livechat.callback.ConnectListener;
import io.rocketchat.livechat.model.GuestObject;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener, AuthListener.LoginListener {

    private LiveChatAPI liveChat;

    public static String userName="guest-18";
    public static String roomId="u7xcgonkr7sh";
    public static String userID="rQ2EHbhjryZnqbZxC";
    public static String visitorToken="707d47ae407b3790465f61d28ee4c63d";
    public static String authToken="VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133";


    public void call(){
        liveChat=new LiveChatAPI("ws://localhost:3000/websocket");
        liveChat.setReconnectionStrategy(null);
        liveChat.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }


    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        liveChat.login(authToken,this);
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Got connect error with the server");
    }


    @Override
    public void onLogin(GuestObject object) {
        System.out.println("login is successful");
    }
}


/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */