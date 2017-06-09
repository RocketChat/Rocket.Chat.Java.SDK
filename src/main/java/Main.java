import io.rocketchat.Utils;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callbacks.*;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;
import io.rocketchat.livechat.models.AgentObject;
import io.rocketchat.livechat.models.GuestObject;
import io.rocketchat.livechat.models.MessageObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sachin on 7/6/17.
 */


public class Main {

    public static String authToken="ubS92xhRYz6pRklXXNxU86z7bzxMo9a4wjq7KtVV8kh";
    public static String visitorToken="gxCgQjdSisYWJGuSf";
    public static String userID="CPse2MSPxc5YbAgzJ";
    public static String roomID="qdyaxcrgqgxl";
    public static String username="guest-5";

    public static void main(String [] args){


//        System.out.println("Hello there");
        final String msgID= Utils.shortUUID();

        final LiveChatAPI liveChat=new LiveChatAPI("ws://localhost:3000/websocket");

        //Connect event to server
        try {
            liveChat.connect();

            liveChat.login(authToken, new GuestCallback() {
                public void call(GuestObject object) {
                    System.out.println("Result is "+object);
//                    liveChat.sendMessage(msgID,roomID,"Hi there",visitorToken);
//                      liveChat.getChatHistory(roomID, 50, new Date(), new MessagesCallback() {
//                          public void call(ArrayList<MessageObject> list, int unreadNotLoaded) {
//                              for (MessageObject object1: list){
//                                  System.out.println("Message is "+object1.getMessage());
//                              }
//                          }
//                      });
//                    liveChat.sendIsTyping(roomID,username,true );
//                    liveChat.getAgentData(roomID, new AgentCallback() {
//                        public void call(AgentObject object) {
//                            System.out.println(object);
//                        }
//                    });

                    liveChat.subscribeRoom(roomID,false);
                    liveChat.subscribeLiveChatRoom(roomID,false);
                    liveChat.subscribeTyping(roomID,false);
                    LiveChatStreamMiddleware.getInstance().subscribeRoom(new MessageCallback() {
                        public void call(String roomId,MessageObject object) {
                            System.out.println("Got message "+object+ " from roomId "+roomId);
                        }
                    });

                    LiveChatStreamMiddleware.getInstance().subscribeTyping(new TypingCallback() {
                        public void call(String roomId, String user, Boolean istyping) {
                            System.out.println(user+" : typing "+istyping);
                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
