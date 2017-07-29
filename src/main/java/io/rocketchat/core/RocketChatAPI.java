package io.rocketchat.core;
import io.rocketchat.common.data.model.Room;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.common.network.Socket;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.core.callback.*;
import io.rocketchat.core.factory.ChatRoomFactory;
import io.rocketchat.core.middleware.CoreMiddleware;
import io.rocketchat.core.middleware.CoreStreamMiddleware;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.rpc.*;
import org.json.JSONArray;
import org.json.JSONObject;

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
    CoreStreamMiddleware coreStreamMiddleware;

// factory class
    ChatRoomFactory factory;

    public RocketChatAPI(String url) {
        super(url);
        integer=new AtomicInteger(1);
        coreMiddleware=CoreMiddleware.getInstance();
        coreStreamMiddleware=CoreStreamMiddleware.getInstance();
        factory=new ChatRoomFactory(this);
    }

    public String getMyUserName(){
        return userInfo.getUserName();
    }

    public JSONArray getMyEmails(){
        return userInfo.getEmails();
    }

    public ChatRoomFactory getFactory() {
        return factory;
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
    public void getPermissions(AccountListener.getPermissionsListener listener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.GETPERMISSIONS);
        sendDataInBackground(AccountRPC.getPermissions(uniqueID,null));
    }

    //Tested
    public void getPublicSettings(AccountListener.getPublicSettingsListener listener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.GETPUBLICSETTINGS);
        sendDataInBackground(AccountRPC.getPublicSettings(uniqueID,null));
    }

    //Tested
    public void getUserRoles(UserListener.getUserRoleListener userRoleListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,userRoleListener, CoreMiddleware.ListenerType.GETUSERROLES);
        sendDataInBackground(BasicRPC.getUserRoles(uniqueID));
    }


    //Tested
    public void listCustomEmoji(EmojiListener listener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.LISTCUSTOMEMOJI);
        sendDataInBackground(BasicRPC.listCustomEmoji(uniqueID));
    }

    //Tested
    public void logout(SimpleListener listener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.LOGOUT);
        sendDataInBackground(BasicRPC.logout(uniqueID));
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
    private void getRoomRoles(String roomId,RoomListener.RoomRolesListener listener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.GETROOMROLES);
        sendDataInBackground(BasicRPC.getRoomRoles(uniqueID,roomId));
    }

    //Tested
    private void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.LOADHISTORY);
        sendDataInBackground(ChatHistoryRPC.loadHistory(uniqueID,roomID,oldestMessageTimestamp,limit,lasttimestamp));
    }

    //Tested
    private void sendIsTyping(String roomId, String username, Boolean istyping){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(TypingRPC.sendTyping(uniqueID,roomId,username,istyping));
    }


    //Tested
    private void sendMessage(String msgId, String roomID, String message, MessageListener.MessageAckListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.SENDMESSAGE);
        sendDataInBackground(MessageRPC.sendMessage(uniqueID,msgId,roomID,message));
    }


    //Tested
    private void deleteMessage(String msgId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.MESSAGEOP);
        sendDataInBackground(MessageRPC.deleteMessage(uniqueID,msgId));
    }

    //Tested
    private void updateMessage (String msgId, String roomId, String message, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.MESSAGEOP);
        sendDataInBackground(MessageRPC.updateMessage(uniqueID,msgId,roomId,message));
    }

    //Tested
    private void pinMessage ( JSONObject message, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.MESSAGEOP);
        sendDataInBackground(MessageRPC.pinMessage(uniqueID,message));
    }

    //Tested
    private void unpinMessage (JSONObject message, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.MESSAGEOP);
        sendDataInBackground(MessageRPC.unpinMessage(uniqueID,message));
    }

    //Tested
    private void starMessage( String msgId, String roomId, Boolean starred, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.MESSAGEOP);
        sendDataInBackground(MessageRPC.starMessage(uniqueID,msgId,roomId,starred));
    }

    //Tested
    private void setReaction (String emojiId, String msgId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.MESSAGEOP);
        sendDataInBackground(MessageRPC.setReaction(uniqueID,emojiId,msgId));
    }

    //Tested
    public void createPublicGroup(String groupName, String [] users, Boolean readOnly,RoomListener.GroupListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.CREATEGROUP);
        sendDataInBackground(RoomRPC.createPublicGroup(uniqueID,groupName, users, readOnly));
    }

    //Tested
    public void createPrivateGroup(String groupName, String [] users, RoomListener.GroupListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.CREATEGROUP);
        sendDataInBackground(RoomRPC.createPrivateGroup(uniqueID,groupName,users));
    }

    //Tested
    private void deleteGroup(String roomId, SimpleListener listener){
        //Apply simpleListener
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.DELETEGROUP);
        sendDataInBackground(RoomRPC.deleteGroup(uniqueID,roomId));
    }

    //Tested
    private void archieveRoom(String roomId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.ARCHIEVE);
        sendDataInBackground(RoomRPC.archieveRoom(uniqueID,roomId));
    }

    //Tested
    private void unarchiveRoom(String roomId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.UNARCHIEVE);
        sendDataInBackground(RoomRPC.unarchiveRoom(uniqueID,roomId));
    }

    //Tested
    public void joinPublicGroup(String roomId, String joinCode, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.JOINPUBLICGROUP);
        sendDataInBackground(RoomRPC.joinPublicGroup(uniqueID,roomId,joinCode));
    }

    //Tested
    private void leaveGroup(String roomId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.LEAVEGROUP);
        sendDataInBackground(RoomRPC.leaveGroup(uniqueID,roomId));
    }

    //Tested
    private void hideRoom(String roomId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.HIDEROOM);
        sendDataInBackground(RoomRPC.hideRoom(uniqueID,roomId));
    }

    //Tested
    private void openRoom(String roomId, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.OPENROOM);
        sendDataInBackground(RoomRPC.openRoom(uniqueID,roomId));
    }

    //Tested
    private void setFavouriteRoom(String roomId, Boolean isFavouriteRoom, SimpleListener listener){
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,listener, CoreMiddleware.ListenerType.SETFAVOURITEROOM);
        sendDataInBackground(RoomRPC.setFavouriteRoom(uniqueID,roomId,isFavouriteRoom));
    }

    //Tested
    public void setStatus(PresenceRPC.Status s){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(PresenceRPC.setDefaultStatus(uniqueID,s));
    }

    //Tested
    private String subscribeRoomMessageEvent(String room_id, Boolean enable, SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener){
        String uniqueID= Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener, CoreStreamMiddleware.SubType.SUBSCRIBEROOMMESSAGE);
        coreStreamMiddleware.subscribeRoomMessage(listener);
        sendDataInBackground(CoreSubRPC.subscribeRoomMessageEvent(uniqueID,room_id,enable));
        return uniqueID;
    }

    private String subscribeRoomTypingEvent(String room_id, Boolean enable, SubscribeListener subscribeListener, TypingListener listener){
        String uniqueID= Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener, CoreStreamMiddleware.SubType.SUBSCRIBEROOMTYPING);
        coreStreamMiddleware.subscribeRoomTyping(listener);
        sendDataInBackground(CoreSubRPC.subscribeRoomTypingEvent(uniqueID,room_id,enable));
        return uniqueID;
    }


    private void unsubscribeRoom(String subId){
        sendDataInBackground(CoreSubRPC.unsubscribeRoom(subId));
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
                coreStreamMiddleware.processSubSuccess(object);
                break;
            case CHANGED:
                coreStreamMiddleware.processCallback(object);
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
     * ChatRoom class to access private methods
     */

    // TODO: 29/7/17 add throw custom exceptions if method call violates permission required to execute given RPC
    public class ChatRoom {

        Room room;

        //Subscription Ids for new subscriptions
        String roomSubId;  // TODO: 29/7/17 check for persistent SubscriptionId of the room
        String typingSubId;

        public ChatRoom(Room room){
            this.room=room;
        }

        public Boolean isSubscriptionObject(){
            return room instanceof SubscriptionObject;
        }

        public Room getRoomData() {
            return room;
        }

        //RPC methods

        public void getRoomRoles(RoomListener.RoomRolesListener listener){
            RocketChatAPI.this.getRoomRoles(room.getRoomId(),listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener){
            RocketChatAPI.this.getChatHistory(room.getRoomId(),limit,oldestMessageTimestamp,lasttimestamp,listener);
        }

        public void sendIsTyping(Boolean istyping){
            RocketChatAPI.this.sendIsTyping(room.getRoomId(),getMyUserName(),istyping);
        }

        public void sendMessage(String message){
            RocketChatAPI.this.sendMessage(Utils.shortUUID(),room.getRoomId(),message,null);
        }

        public void sendMessage(String message, MessageListener.MessageAckListener listener){
            RocketChatAPI.this.sendMessage(Utils.shortUUID(),room.getRoomId(),message,listener);
        }

        // TODO: 27/7/17 Need more attention on replying to message
        private void replyMessage(RocketChatMessage msg, String message, MessageListener.MessageAckListener listener){
            message="[ ](?msg="+msg.getMessageId()+") @"+msg.getSender().getUserName()+" "+message;
            RocketChatAPI.this.sendMessage(Utils.shortUUID(),room.getRoomId(),message,listener);
        }

        public void deleteMessage(String msgId, SimpleListener listener){
            RocketChatAPI.this.deleteMessage(msgId ,listener);
        }

        public void updateMessage (String msgId, String message, SimpleListener listener){
            RocketChatAPI.this.updateMessage(msgId, room.getRoomId(), message, listener);
        }

        public void pinMessage ( JSONObject message, SimpleListener listener){
            RocketChatAPI.this.pinMessage(message, listener);
        }

        public void unpinMessage (JSONObject message, SimpleListener listener){
            RocketChatAPI.this.unpinMessage(message, listener);
        }

        public void starMessage( String msgId, Boolean starred, SimpleListener listener){
            RocketChatAPI.this.starMessage(msgId, room.getRoomId(),starred, listener);
        }

        public void setReaction (String emojiId, String msgId, SimpleListener listener){
            RocketChatAPI.this.setReaction(emojiId,msgId,listener);
        }

        public void deleteGroup(SimpleListener listener){
            RocketChatAPI.this.deleteGroup(room.getRoomId(),listener);
        }

        public void archieve(SimpleListener listener){
            RocketChatAPI.this.archieveRoom(room.getRoomId(),listener);
        }

        public void unarchieve(SimpleListener listener){
            RocketChatAPI.this.unarchiveRoom(room.getRoomId(),listener);
        }

        public void leave(SimpleListener listener){
            RocketChatAPI.this.leaveGroup(room.getRoomId(),listener);
        }

        public void hide(SimpleListener listener){
            RocketChatAPI.this.hideRoom(room.getRoomId(),listener);
        }

        public void open(SimpleListener listener){
            RocketChatAPI.this.openRoom(room.getRoomId(),listener);
        }

        public void setFavourite(Boolean isFavoutite, SimpleListener listener){
            RocketChatAPI.this.setFavouriteRoom(room.getRoomId(),isFavoutite,listener);
        }

        //Subscription methods

        public void subscribeRoomMessageEvent(SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener){
            if (roomSubId==null) {
                roomSubId=RocketChatAPI.this.subscribeRoomMessageEvent(room.getRoomId(), true, subscribeListener, listener);
            }
        }

        public void subscribeRoomTypingEvent(SubscribeListener subscribeListener, TypingListener listener){
            if (typingSubId==null){
                typingSubId= RocketChatAPI.this.subscribeRoomTypingEvent(room.getRoomId(), true, subscribeListener, listener);
            }
        }

        public void unSubscribeRoomMessageEvent(){
            if (roomSubId!=null) {
                RocketChatAPI.this.unsubscribeRoom(roomSubId);
                roomSubId = null;
            }
        }

        public void unSubscribeRoomTypingEvent(){
            if (typingSubId!=null) {
                RocketChatAPI.this.unsubscribeRoom(typingSubId);
                typingSubId = null;
            }
        }

        public void unSubscribeAllEvents(){
            unSubscribeRoomMessageEvent();
            unSubscribeRoomTypingEvent();
        }

        // TODO: 29/7/17 refresh methods to be added, changing data should change internal data, maintain state of the room
    }
}
