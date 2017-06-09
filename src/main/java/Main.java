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

    public static void main(String [] args){

//        System.out.println("Hello there");
        final LiveChatAPI liveChat=new LiveChatAPI("wss://demo.rocket.chat/websocket");

        //Connect event to server
        try {
            liveChat.connect();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    liveChat.getInitialData(new InitialDataCallback() {
                        public void call(LiveChatConfigObject object) {
                            System.out.println("First result is "+object);
                      liveChat.registerGuest("sac", "sac@gmail.com", object.getDepartments().get(0).getId(), new GuestCallback() {
                          public void call(GuestObject object) {
                              System.out.println("Second result is "+object);
                              liveChat.login(object.getToken(), new GuestCallback() {
                                  public void call(GuestObject object) {
                                      System.out.println("Third result is "+object);
                                  }
                              });
                          }
                      });
                        }
                    });
                }
            },3000);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
