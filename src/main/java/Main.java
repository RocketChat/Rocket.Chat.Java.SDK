import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.ConnectListener;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener {

    private LiveChatAPI liveChat;
    private LiveChatAPI.ChatRoom chatRoom;

    public void call(){
        liveChat=new LiveChatAPI("wss://demo.rocket.chat/websocket");
        liveChat.connect(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Got connect error with the server");
    }

}
