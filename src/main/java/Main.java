import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.callback.SubscriptionListener;
import io.rocketchat.core.factory.ChatRoomFactory;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;

import java.util.ArrayList;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener, LoginListener, SubscriptionListener.GetSubscriptionListener {


    RocketChatAPI api;
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
            api.getSubscriptions(this);
        }else{
            System.out.println("Got error "+error.getMessage());
        }
    }

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {

        //Creating Logical ChatRooms using factory class
        ChatRoomFactory factory=api.getFactory();

        /* Number of operations can be performed on below room object like
         * sendMessage
         * deleteMessage
         * pinMessage
         * leaveRoom
         * starRoom
         * hideRoom
         * etc
         * Note : This ChatRoom is logical, meant to perform operations
         */

        RocketChatAPI.ChatRoom room=factory.createChatRooms(subscriptions).getChatRoomByName("general");   //This should exist on server

        //sending sample message to general

        room.sendMessage("Hi there, message sent via code..");


        /*
        Getting list of rooms from factory class after creating logical ChatRooms
         */

        ArrayList <RocketChatAPI.ChatRoom> rooms=factory.getChatRooms();

        for (RocketChatAPI.ChatRoom myroom : rooms){
            System.out.println("Room id is "+myroom.getRoomData().getRoomId());
            System.out.println("room name is "+myroom.getRoomData().getRoomName());
            System.out.println("Room type is "+myroom.getRoomData().getRoomType());
        }

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
