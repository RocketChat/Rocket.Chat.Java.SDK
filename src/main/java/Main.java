import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callbacks.ConnectCallback;
import io.rocketchat.livechat.callbacks.GuestCallback;
import io.rocketchat.livechat.callbacks.HistoryCallback;
import io.rocketchat.livechat.models.GuestObject;
import io.rocketchat.livechat.models.MessageObject;
import io.rocketchat.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sachin on 7/6/17.
 */


public class Main implements ConnectCallback,
        GuestCallback,
        HistoryCallback {

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
//                    liveChat.getAgentData(roomID, new AgentCallback() {
//                        public void call(AgentObject object) {
//                            System.out.println(object);
//                        }
//                    });

//                    liveChat.subscribeRoom(roomID,false);
//                    liveChat.subscribeLiveChatRoom(roomID,false);
//                    liveChat.subscribeTyping(roomID,false);
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
    public void call(GuestObject object) {
        System.out.println("Login is successful");
        liveChat.getChatHistory(roomID, 50, new Date(),this);
    }

    @Override
    public void call(ArrayList<MessageObject> list, int unreadNotLoaded) {
        for (MessageObject object: list){
            System.out.println("Message is "+object.getMessage());
        }
    }

}
