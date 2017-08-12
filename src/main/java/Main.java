import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;

import java.util.List;

/**
 * Created by sachin on 7/6/17.
 */

public class Main extends CoreAdapter {

    private static String serverurl = "wss://demo.rocket.chat/websocket";
    RocketChatAPI api;

    public static void main(String[] args) {
        new Main().call();
    }

    public void call() {
        api = new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.setPingInterval(3000);
        api.connect(this);

    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login("testuserrocks","testuserrocks",this);
    }

    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Got connect error here");
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);
    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        RocketChatAPI.ChatRoom room = api.getChatRoomFactory().createChatRooms(subscriptions).getChatRoomByName("sachin.shinde");

        room.subscribeRoomTypingEvent(new SubscribeListener() {
            @Override
            public void onSubscribe(Boolean isSubscribed, String subId) {
                System.out.println("subscribed to typing successfully");
            }
        },this);

        room.subscribeRoomMessageEvent(new SubscribeListener() {
            @Override
            public void onSubscribe(Boolean isSubscribed, String subId) {
                System.out.println("subscribed for receiving messages");
            }
        }, this);
    }



    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {
        System.out.println("got typing from "+ user +" "+istyping);
    }

    @Override
    public void onMessage(String roomId, RocketChatMessage message) {
        System.out.println("got message "+message.getMessage());
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnect detected");
    }
}

/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

