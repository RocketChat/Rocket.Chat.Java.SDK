import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.factory.ChatRoomFactory;
import io.rocketchat.core.middleware.CoreStreamMiddleware;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.TokenObject;

import java.util.ArrayList;

/**
 * Created by sachin on 7/6/17.
 */

public class Main extends CoreAdapter{


    RocketChatAPI api;
    private static String serverurl="ws://localhost:3000/websocket";
    private RocketChatAPI.ChatRoom room;

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
        System.out.println("Connected to server with id "+sessionID);
        api.login("sachin","sachin9922",this);
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
    public void onLogin(TokenObject token, ErrorObject error) {
        System.out.println("Logged in successfully with token " + token);
        api.getRooms(this);
    }

    @Override
    public void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error) {
        ChatRoomFactory factory=api.getFactory();
        room=factory.createChatRooms(rooms).getChatRoomByName("mypublicchannel");
        System.out.println("This is the room with id "+room.getRoomData().getRoomId());
        room.subscribeRoomTypingEvent(new SubscribeListener() {
            @Override
            public void onSubscribe(Boolean success, String subId) {
                if (success){
                    System.out.println("Subscribed to typing");
                }
            }
        },this);
    }

    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {
        System.out.println("Typing event arrived on roomId "+roomId +" by user "+user+ " typing is "+istyping);
        room.unSubscribeRoomTypingEvent();
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
