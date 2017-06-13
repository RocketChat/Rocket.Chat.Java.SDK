import io.rocketchat.common.utils.Utils;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.MessageObject;

import java.util.ArrayList;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectCallback,
        GuestCallback,
        HistoryCallback{

    public static String authToken="ubS92xhRYz6pRklXXNxU86z7bzxMo9a4wjq7KtVV8kh";
    public static String visitorToken="gxCgQjdSisYWJGuSf";
    public static String userID="CPse2MSPxc5YbAgzJ";
    public static String roomID="qdyaxcrgqgxl";
    public static String username="guest-5";

    LiveChatAPI liveChat;
    private String msgID;

    public void call(){
        msgID= Utils.shortUUID();

        liveChat=new LiveChatAPI("ws://localhost:3000/websocket");
        liveChat.connectAsync(this);

//                    liveChat.sendMessage(msgID,roomID,"Hi there",visitorToken);
//                    liveChat.sendIsTyping(roomID,username,true );



//                    LiveChatStreamMiddleware.getInstance().subscribeRoom(new MessageCallback() {
//                        public void call(String roomId,MessageObject object) {
//                            System.out.println("Got message "+object+ " from roomId "+roomId);
//                        }
//                    });
//
//                    LiveChatStreamMiddleware.getInstance().subscribeTyping(new TypingCallback() {
//                        public void call(String roomId, String user, Boolean istyping) {
//                            System.out.println(user+" : typing "+istyping);
//                        }
//                    });

    }
    public static void main(String [] args){


//        System.out.println("Hello there");
        new Main().call();


    }


    @Override
    public void onConnect(String sessionID) {
        System.out.println("on connect got called");
        liveChat.login(authToken,Main.this);
    }

    @Override
    public void call(LiveChatMiddleware.CallbackType guestCallbackType, GuestObject object) {
        switch (guestCallbackType) {
            case REGISTER:
                System.out.println("This is registration");
                break;
            case LOGIN:
                System.out.println("This is login");
                break;
        }
    }

    @Override
    public void call(ArrayList<MessageObject> list, int unreadNotLoaded) {
        for (MessageObject object: list){
            System.out.println("Message is "+object);
        }
    }

}
