import io.rocketchat.Socket;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.livechat.LiveChatAPI;

import java.io.IOException;

/**
 * Created by sachin on 7/6/17.
 */
public class Main {
    public static void main(String [] args){

//        System.out.println("Hello there");
        RocketChatAPI liveChat=new RocketChatAPI("ws://localhost:3000/websocket");

        //Connect event to server
        try {
            liveChat.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
