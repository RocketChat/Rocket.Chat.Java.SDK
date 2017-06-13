import io.rocketchat.common.utils.Utils;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.AuthListener;
import io.rocketchat.livechat.callback.ConnectListener;
import io.rocketchat.livechat.callback.LoadHistoryListener;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.MessageObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener,
        AuthListener.LoginListener, LoadHistoryListener {
    /**
     * Those parameters are supposed to be available all the time, need to create a wrapper that abstracts API
     */
    /**
     * Availabel after login
     */



    public static String authToken="ubS92xhRYz6pRklXXNxU86z7bzxMo9a4wjq7KtVV8kh"; //Get after login or register
    public static String visitorToken="gxCgQjdSisYWJGuSf";                        //Created before login
    public static String userID="CPse2MSPxc5YbAgzJ";                              //after login
    public static String roomID="qdyaxcrgqgxl";                                   //Created during sending message
    public static String username="guest-5";                                      //After login it is available


    LiveChatAPI liveChat;
    private String msgID;

    public void call(){
        msgID= Utils.shortUUID();
        liveChat=new LiveChatAPI("ws://localhost:3000/websocket");
        liveChat.connectAsync(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("on connect got called");
        liveChat.login(authToken,Main.this);
    }

    @Override
    public void onLogin(final GuestObject object) {
        System.out.println("Login successful");
        liveChat.getChatHistory(roomID,40,null,new Date(),this);
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onLoadHistory(ArrayList<MessageObject> list, int unreadNotLoaded) {
        System.out.println("Messages received");
    }
}
