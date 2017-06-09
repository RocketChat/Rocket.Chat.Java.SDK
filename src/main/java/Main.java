import io.rocketchat.Socket;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callbacks.GuestCallback;
import io.rocketchat.livechat.callbacks.InitialDataCallback;
import io.rocketchat.livechat.models.GuestObject;
import io.rocketchat.livechat.models.LiveChatConfigObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sachin on 7/6/17.
 */

public class Main {

    public static String token="49MOBz4cO8LUKggSnu2gBz-lYPbhFWxDgN9q3Z8ZPQE";

    public static void main(String [] args){

//        System.out.println("Hello there");
        final LiveChatAPI liveChat=new LiveChatAPI("wss://demo.rocket.chat/websocket");

        //Connect event to server
        try {
            liveChat.connect();

            liveChat.login(token, new GuestCallback() {
                public void call(GuestObject object) {
                    System.out.println("Result is "+object);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
