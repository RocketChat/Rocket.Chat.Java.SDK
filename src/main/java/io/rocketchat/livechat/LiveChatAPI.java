package io.rocketchat.livechat;

import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.network.Socket;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;
import io.rocketchat.livechat.rpc.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */

//Todo Make network layer pluggable (Any websocket library can be used with SDK)

public class LiveChatAPI extends Socket{

    AtomicInteger integer;
    String sessionId;
    JSONObject userInfo;

    ConnectListener connectListener;

    LiveChatMiddleware liveChatMiddleware;
    LiveChatStreamMiddleware liveChatStreamMiddleware;

    public LiveChatAPI(String url) {
        super(url);
        integer=new AtomicInteger(1);
        liveChatMiddleware =LiveChatMiddleware.getInstance();
        liveChatStreamMiddleware=LiveChatStreamMiddleware.getInstance();
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void getInitialData(final InitialDataListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETINITIALDATA);
        sendDataInBackground(LiveChatBasicRPC.getInitialData(uniqueID));
    }

    public void registerGuest(final String name, final String email, final String dept, final AuthListener.RegisterListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.REGISTER);
        sendDataInBackground(LiveChatBasicRPC.registerGuest(uniqueID,name,email,dept));
    }

    public void login(final String token, final AuthListener.LoginListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.LOGIN);
        sendDataInBackground(LiveChatBasicRPC.login(uniqueID,token));
    }


    public void sendOfflineMessage(final String name, final String email, final String message){
        int uniqueID=integer.getAndIncrement();
        sendDataInBackground(LiveChatBasicRPC.sendOfflineMessage(uniqueID,name,email,message));
    }

    public void sendOfflineMessage(final String name, final String email, final String message, final MessageListener.OfflineMessageListener listener){
        int uniqueID=integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.SENDOFFLINEMESSAGE);
        sendDataInBackground(LiveChatBasicRPC.sendOfflineMessage(uniqueID,name,email,message));
    }


    private void getChatHistory(final String roomID, final int limit, final Date oldestMessageTimestamp, final Date lasttimestamp, final LoadHistoryListener listener){
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETCHATHISTORY);
        sendDataInBackground(LiveChatHistoryRPC.loadHistory(uniqueID,roomID,oldestMessageTimestamp,limit,lasttimestamp));
    }


    private void getAgentData(final String roomId, final AgentListener.AgentDataListener listener){
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETAGENTDATA);
        sendDataInBackground(LiveChatBasicRPC.getAgentData(uniqueID,roomId));
    }


    private void sendMessage(final String msgId, final String roomID, final String message, final String token){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
    }

    private void sendMessage(final String msgId, final String roomID, final String message, final String token, final MessageListener.MessageAckListener messageAckListener){
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID,messageAckListener, LiveChatMiddleware.ListenerType.SENDMESSAGE);
        sendDataInBackground(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
    }

    private void sendIsTyping(final String roomId, final String username, final Boolean istyping){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(LiveChatTypingRPC.streamNotifyRoom(uniqueID,roomId,username,istyping));
    }


    private void subscribeRoom(final String roomID, final Boolean enable, final SubscribeListener subscribeListener, final MessageListener.SubscriptionListener listener){

        String uniqueID=Utils.shortUUID();
        if (subscribeListener !=null) {
            liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener, LiveChatStreamMiddleware.SubType.STREAMROOMMESSAGES);
        }
        if (listener!=null){
            liveChatStreamMiddleware.subscribeRoom(listener);
        }
        sendDataInBackground(LiveChatSubRPC.streamRoomMessages(uniqueID,roomID,enable));

    }

    private void subscribeLiveChatRoom(final String roomID, final Boolean enable, final SubscribeListener subscribeListener, final AgentListener.AgentConnectListener agentConnectListener){

        String uniqueID=Utils.shortUUID();
        if (subscribeListener !=null) {
            liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener, LiveChatStreamMiddleware.SubType.STREAMLIVECHATROOM);
        }
        if (agentConnectListener !=null){
            liveChatStreamMiddleware.subscribeLiveChatRoom(agentConnectListener);
        }
        sendDataInBackground(LiveChatSubRPC.streamLivechatRoom(uniqueID,roomID,enable));

    }

    private void subscribeTyping(final String roomID, final Boolean enable, final SubscribeListener subscribeListener, final TypingListener listener){

        String uniqueID=Utils.shortUUID();
        if (subscribeListener !=null) {
            liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener, LiveChatStreamMiddleware.SubType.NOTIFYROOM);
        }
        if (listener!=null){
            liveChatStreamMiddleware.subscribeTyping(listener);
        }
        sendDataInBackground(LiveChatSubRPC.subscribeTyping(uniqueID,roomID,enable));

    }

    private void closeConversation(final String roomId){
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(LiveChatBasicRPC.closeConversation(uniqueID,roomId));
    }

    public void connect(ConnectListener connectListener) {
        createSocket();
        this.connectListener = connectListener;
        super.connectAsync();
    }

    @Override
    protected void onConnected() {
        integer.set(1);
        sendDataInBackground(LiveChatBasicRPC.ConnectObject());
        super.onConnected();
    }

    @Override
    protected void onTextMessage(String text) throws Exception{
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
                    userInfo = object.optJSONObject("fields");
                }
                break;
            case RESULT:
                liveChatMiddleware.processCallback(Long.valueOf(object.optString("id")), object);
                break;
            case READY:
                liveChatStreamMiddleware.processSubSuccess(object);
                break;
            case CHANGED:
                liveChatStreamMiddleware.processCallback(object);
                break;
            case OTHER:
                //DO SOMETHING
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


    public class ChatRoom{

        String userName;
        String roomId;
        String userId;
        String visitorToken;
        String authToken;

        public ChatRoom(String userName, String roomId, String userId, String visitorToken, String authToken) {
            this.userName = userName;
            this.roomId = roomId;
            this.userId = userId;
            this.visitorToken = visitorToken;
            this.authToken = authToken;
        }

        public ChatRoom(String s){
            try {
                JSONObject object=new JSONObject(s);
                this.userName=object.getString("userName");
                this.roomId=object.getString("roomId");
                this.userId=object.getString("userId");
                this.visitorToken=object.getString("visitorToken");
                this.authToken=object.getString("authToken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void login(AuthListener.LoginListener listener){
            LiveChatAPI.this.login(authToken,listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp,Date lasttimestamp,LoadHistoryListener listener){
            LiveChatAPI.this.getChatHistory(roomId,limit,oldestMessageTimestamp,lasttimestamp,listener);
        }

        public void getAgentData(AgentListener.AgentDataListener listener){
            LiveChatAPI.this.getAgentData(roomId,listener);
        }

        /**
         * Used for sending messages to server
         * @param message to be sent
         * @return MessageID
         */
        public String sendMessage(String message){
            String uuid=Utils.shortUUID();
            LiveChatAPI.this.sendMessage(uuid,roomId,message,visitorToken);
            return uuid;
        }

        /**
         * Used for sending messages to server with messageAcknowledgement
         * @param message
         * @param messageAckListener Returns ack to particular message
         * @return MessageID
         */

        public String sendMessage(String message,MessageListener.MessageAckListener messageAckListener){
            String uuid=Utils.shortUUID();
            LiveChatAPI.this.sendMessage(uuid,roomId,message,visitorToken,messageAckListener);
            return uuid;
        }

        public void sendIsTyping(Boolean istyping){
            LiveChatAPI.this.sendIsTyping(roomId,userName,istyping);
        }

        public void subscribeRoom(SubscribeListener subscribeListener,MessageListener.SubscriptionListener listener){
            LiveChatAPI.this.subscribeRoom(roomId,false,subscribeListener,listener);
        }

        public void subscribeLiveChatRoom(SubscribeListener subscribeListener,AgentListener.AgentConnectListener agentConnectListener){
            LiveChatAPI.this.subscribeLiveChatRoom(roomId,false,subscribeListener,agentConnectListener);
        }

        public void subscribeTyping(SubscribeListener subscribeListener,TypingListener listener){
            LiveChatAPI.this.subscribeTyping(roomId,false,subscribeListener,listener);
        }

        public void closeConversation(){
            LiveChatAPI.this.closeConversation(roomId);
        }



        public String getUserName() {
            return userName;
        }

        public String getRoomId() {
            return roomId;
        }

        public String getUserId() {
            return userId;
        }

        public String getVisitorToken() {
            return visitorToken;
        }

        public String getAuthToken() {
            return authToken;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"userName\":\"" + userName + '\"' +
                    ",\"roomId\":\"" + roomId + '\"' +
                    ",\"userId\":\"" + userId + '\"' +
                    ",\"visitorToken\":\"" + visitorToken + '\"' +
                    ",\"authToken\":\"" + authToken + '\"' +
                    '}';
        }
    }

    public ChatRoom createRoom(String userID,String authToken){
        String userName = null;
        if (userInfo!=null) {
            userName = userInfo.optString("username");
        }
        String visitorToken= LiveChatBasicRPC.visitorToken;
        String roomID=Utils.shortUUID();
        return new ChatRoom(userName,roomID,userID,visitorToken,authToken);
    }

    public ChatRoom createRoom(String s){
        return new ChatRoom(s);
    }
}
