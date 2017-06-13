import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.GuestObject;
import io.rocketchat.livechat.model.LiveChatConfigObject;
import io.rocketchat.livechat.model.MessageObject;

import java.util.ArrayList;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements ConnectListener,
        AuthListener.LoginListener, LoadHistoryListener, InitialDataListener, AuthListener.RegisterListener ,
        AgentListener.AgentConnectListener, MessageListener {

    private LiveChatAPI liveChat;
    private LiveChatAPI.ChatRoom chatRoom;

    public void call(){
        liveChat=new LiveChatAPI("ws://localhost:3000/websocket");
        liveChat.connectAsync(this);
    }

    public static void main(String [] args){
        new Main().call();
    }

    @Override
    public void onConnect(String sessionID) {
        liveChat.getInitialData(this);
        liveChat.registerGuest("titanic","titanic@gmail.com",null,this);
    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onLoadHistory(ArrayList<MessageObject> list, int unreadNotLoaded) {
        System.out.println("Messages received");
    }

    @Override
    public void onLogin(GuestObject object) {
        chatRoom=liveChat.createRoom(object.getUserID(),object.getToken());
        chatRoom.subscribeLiveChatRoom(null,this);
        System.out.println("Chatroom is "+chatRoom);
        chatRoom.sendMessage("Hi, anyone there please?");
    }

    @Override
    public void onInitialData(LiveChatConfigObject object) {
        System.out.println("Initial data is "+object);
    }

    @Override
    public void onRegister(GuestObject object) {
        System.out.println("Registration success "+object);
        liveChat.login(object.getToken(),this);
    }

    @Override
    public void onAgentConnect(AgentObject agentObject) {
        System.out.println("New agent got connected"+agentObject.getUsername());
        chatRoom.subscribeRoom(null,this);
    }

    @Override
    public void onMessage(String roomId, MessageObject object) {
        System.out.println("got message "+object.getMessage());
    }

    @Override
    public void onAgentDisconnect(String roomId, MessageObject object) {
        System.out.println("Agent got disconnected");
    }
}
