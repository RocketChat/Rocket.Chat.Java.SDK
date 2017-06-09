import io.rocketchat.Utils;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callbacks.GuestCallback;
import io.rocketchat.livechat.models.GuestObject;

import java.io.IOException;

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

        final String roomID= Utils.shortUUID();
        System.out.println("roomID is "+roomID);

        final LiveChatAPI liveChat=new LiveChatAPI("ws://localhost:3000/websocket");

        //Connect event to server
        try {
            liveChat.connect();

            liveChat.login(authToken, new GuestCallback() {
                public void call(GuestObject object) {
                    System.out.println("Result is "+object);
                    liveChat.sendMessage(msgID,roomID,"Hi there",visitorToken);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
