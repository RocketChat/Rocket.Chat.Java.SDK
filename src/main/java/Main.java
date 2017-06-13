import io.rocketchat.common.utils.Utils;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.MessageObject;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener,
        AuthListener.LoginListener,
        MessageListener,
        TypingListener, AgentListener.AgentConnectListener {

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
        liveChat.subscribeLiveChatRoom(roomID,false,null,this);
        liveChat.subscribeRoom(roomID,false,null,this);
        liveChat.subscribeTyping(roomID,false,null,this);
        liveChat.sendMessage(Utils.shortUUID(),roomID,"Hi gandu",visitorToken);
    }

    @Override
    public void onMessage(String roomId, MessageObject object) {
        System.out.println("got message from "+roomId+" message "+object);
    }

    @Override
    public void onTyping(String roomId, String user, Boolean istyping) {
        System.out.println("user "+user+" typing :"+istyping);
    }

    @Override
    public void onAgentConnect(AgentObject agentObject) {
        System.out.println("Agent connected");
    }

}
