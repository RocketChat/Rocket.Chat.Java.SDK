import io.rocketchat.Utils;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callbacks.GuestCallback;
import io.rocketchat.livechat.callbacks.MessagesCallback;
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
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
