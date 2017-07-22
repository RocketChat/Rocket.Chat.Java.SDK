package io.rocketchat.core;
import io.rocketchat.common.data.model.Room;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.network.Socket;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.core.callback.*;
import io.rocketchat.core.middleware.CoreMiddleware;
import io.rocketchat.core.middleware.CoreStreamMiddleware;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.rpc.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */

public class RocketChatAPI extends Socket {

    AtomicInteger integer;
    String sessionId;
    UserObject userInfo;

    ConnectListener connectListener;

    CoreMiddleware coreMiddleware;
    CoreStreamMiddleware streamMiddleware;

    ArrayList <ChatRoom> rooms;


    public RocketChatAPI(String url) {
        super(url);
        integer=new AtomicInteger(1);
        coreMiddleware=CoreMiddleware.getInstance();
    }

    public String getMyUserName(){
        return userInfo.getUserName();
    }

    public JSONArray getMyEmails(){
        return userInfo.getEmails();
    }

    //Tested
    public void login(String username, String password, LoginListener loginListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,loginListener, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.login(uniqueID,username,password));
    }

    //Tested
    public void loginUsingToken(String token,LoginListener loginListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,loginListener, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.loginUsingToken(uniqueID,token));
    }

    //Tested
    public void getUserRoles(UserListener.getUserRoleListener userRoleListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,userRoleListener, CoreMiddleware.ListenerType.GETUSERROLES);
        sendDataInBackground(BasicRPC.getUserRoles(uniqueID));
    }


    //Tested
    public void getSubscriptions(SubscriptionListener.GetSubscriptionListener getSubscriptionListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,getSubscriptionListener, CoreMiddleware.ListenerType.GETSUBSCRIPTIONS);
        sendDataInBackground(BasicRPC.getSubscriptions(uniqueID));
    }


    //Tested
    public void getRooms(RoomListener.GetRoomListener getRoomListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,getRoomListener, CoreMiddleware.ListenerType.GETROOMS);
        sendDataInBackground(BasicRPC.getRooms(uniqueID));
    }

    //Tested
    public void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.LOADHISTORY);
        sendDataInBackground(ChatHistoryRPC.loadHistory(uniqueID,roomID,oldestMessageTimestamp,limit,lasttimestamp));
    }

    public void sendIsTyping(String roomId, String username, Boolean istyping){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(TypingRPC.sendTyping(uniqueID,roomId,username,istyping));
    }

    public void sendMessage(String msgId, String roomID, String message){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(MessageRPC.sendMessage(uniqueID,msgId,roomID,message));
    }

    public void sendMessage(String msgId, String roomID, String message, MessageListener.MessageAckListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.SENDMESSAGE);
        sendDataInBackground(MessageRPC.sendMessage(uniqueID,msgId,roomID,message));
    }

    public void setStatus(PresenceRPC.Status s){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(PresenceRPC.setDefaultStatus(uniqueID,s));
    }

    public void subscribeRoom(String room_id){
        String uniqueID= Utils.shortUUID();
        sendDataInBackground(SubscriptionRPC.subscribeRoom(uniqueID,room_id));
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void connect(ConnectListener connectListener){
        createSocket();
        this.connectListener = connectListener;
        super.connectAsync();
    }

    @Override
    protected void onConnected() {
        integer.set(1);
        sendDataInBackground(BasicRPC.ConnectObject());
        super.onConnected();
    }

    @Override
    protected void onTextMessage(String text) throws Exception {
        JSONObject object = new JSONObject(text);
        switch (RPC.parse(object.optString("msg"))) {
            case PING:
                sendDataInBackground("{\"msg\":\"pong\"}");
                break;
            case CONNECTED:
                sessionId = object.optString("session");
                if (connectListener != null) {
                    connectListener.onConnect(sessionId);
                }
                break;
            case ADDED:
                if (object.optString("collection").equals("users")) {
                    userInfo = new UserObject(object.optJSONObject("fields"));
                }
                break;
            case RESULT:
                coreMiddleware.processCallback(Long.valueOf(object.optString("id")), object);
                break;
            case READY:
                break;
            case CHANGED:
                break;
            case OTHER:
                break;
        }

        super.onTextMessage(text);
    }

    @Override
    protected void onConnectError(Exception websocketException) {
        if (connectListener!=null) {
            connectListener.onConnectError(websocketException);
        }
        super.onConnectError(websocketException);
    }

    @Override
    protected void onDisconnected(boolean closedByServer) {
        if (connectListener!=null) {
            connectListener.onDisconnect(closedByServer);
        }
        super.onDisconnected(closedByServer);
    }


    /**
     * ChatRoom APIS
     */

    public ArrayList <ChatRoom> createChatRooms(ArrayList <Room> roomObjects){
        rooms=new ArrayList<>();
        for (Room room : roomObjects){
            rooms.add(getChatRoom(room));
        }
        return rooms;
    }

    public ChatRoom getChatRoom(Room room){
        return new ChatRoom(room);
    }

    public ArrayList <ChatRoom> getChatRooms(){
        return rooms;
    }

    class ChatRoom {

        Room room;

        public ChatRoom(Room room){
            this.room=room;
        }

        public Boolean isSubscriptionObject(){
            return room instanceof SubscriptionObject;
        }

        public Room getRoomData() {
            return room;
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener){
            RocketChatAPI.this.getChatHistory(room.getRoomId(),limit,oldestMessageTimestamp,lasttimestamp,listener);
        }

        public void sendIsTyping(Boolean istyping){
            RocketChatAPI.this.sendIsTyping(room.getRoomId(),getMyUserName(),istyping);
        }

        public void sendMessage(String message){
            RocketChatAPI.this.sendMessage(Utils.shortUUID(),room.getRoomId(),message);
        }

        public void sendMessage(String message, MessageListener.MessageAckListener listener){
            RocketChatAPI.this.sendMessage(Utils.shortUUID(),room.getRoomId(),message,listener);
        }

        public void subscribeRoom(){
            RocketChatAPI.this.subscribeRoom(room.getRoomId());
        }

    }

}
