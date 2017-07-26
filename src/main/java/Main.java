import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.common.utils.EmojiSheet;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.callback.adapter.CoreAdapter;
import io.rocketchat.core.model.*;

import java.util.ArrayList;
import java.util.Date;

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
        System.out.println("Logged in successfully with token "+token);
        api.getRooms(this);
    }

    @Override
    public void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error) {
        System.out.println("Name is "+rooms.get(0).getRoomName());
        room=api.createChatRoom(rooms.get(0));
        room.getChatHistory(20,new Date(),null, this);
    }

    @Override
    public void onLoadHistory(ArrayList<RocketChatMessage> list, int unreadNotLoaded, ErrorObject error) {
        System.out.println("First message is "+list.get(0).getMessage());
    }
}


/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */


