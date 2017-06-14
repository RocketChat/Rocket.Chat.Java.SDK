package io.rocketchat.livechat;

import com.neovisionaries.ws.client.*;
import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.livechat.callback.*;
import io.rocketchat.common.network.EventThread;
import io.rocketchat.common.network.Socket;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;
import io.rocketchat.livechat.rpc.*;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */

public class LiveChatAPI extends Socket{

    AtomicInteger integer;
    String sessionId;
    public JSONObject userInfo;

    WebSocketAdapter adapter;
    LiveChatMiddleware liveChatMiddleware;
    LiveChatStreamMiddleware liveChatStreamMiddleware;

    ConnectListener connectListener;


    public LiveChatAPI(String url) {
        super(url);
        adapter= getAdapter();
        integer=new AtomicInteger(1);
        liveChatMiddleware =LiveChatMiddleware.getInstance();
        liveChatStreamMiddleware=LiveChatStreamMiddleware.getInstance();
    }

    public void getInitialData(final InitialDataListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETINITIALDATA);
                ws.sendText(LiveChatBasicRPC.getInitialData(uniqueID));
            }
        });


    }

    public void registerGuest(final String name, final String email, final String dept, final AuthListener.RegisterListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.REGISTER);
                ws.sendText(LiveChatBasicRPC.registerGuest(uniqueID,name,email,dept));
            }
        });

    }

    public void login(final String token, final AuthListener.LoginListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.LOGIN);
                ws.sendText(LiveChatBasicRPC.login(uniqueID,token));
            }
        });
    }


    private void getChatHistory(final String roomID, final int limit, final Date oldestMessageTimestamp, final Date lasttimestamp, final LoadHistoryListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID = integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETCHATHISTORY);
                ws.sendText(LiveChatHistoryRPC.loadHistory(uniqueID,roomID,oldestMessageTimestamp,limit,lasttimestamp));
            }
        });
    }


    private void getAgentData(final String roomId, final AgentListener.AgentDataListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID = integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,listener, LiveChatMiddleware.ListenerType.GETAGENTDATA);
                ws.sendText(LiveChatBasicRPC.getAgentData(uniqueID,roomId));
            }
        });
    }


    private void sendMessage(final String msgId, final String roomID, final String message, final String token){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID = integer.getAndIncrement();
                ws.sendText(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
            }
        });
    }

    private void sendIsTyping(final String roomId, final String username, final Boolean istyping){
        EventThread.exec(new Runnable() {
            @Override
            public void run() {
                int uniqueID = integer.getAndIncrement();
                ws.sendText(LiveChatTypingRPC.streamNotifyRoom(uniqueID,roomId,username,istyping));
            }
        });
    }


    private void subscribeRoom(final String roomID, final Boolean enable, final SubscribeListener subscribeListener, final MessageListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                if (subscribeListener !=null) {
                    liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener, LiveChatStreamMiddleware.SubType.STREAMROOMMESSAGES);
                }
                if (listener!=null){
                    liveChatStreamMiddleware.subscribeRoom(listener);
                }
                ws.sendText(LiveChatSubRPC.streamRoomMessages(uniqueID,roomID,enable));
            }
        });
    }

    private void subscribeLiveChatRoom(final String roomID, final Boolean enable, final SubscribeListener subscribeListener, final AgentListener.AgentConnectListener agentConnectListener){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                if (subscribeListener !=null) {
                    liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener, LiveChatStreamMiddleware.SubType.STREAMLIVECHATROOM);
                }
                if (agentConnectListener !=null){
                    liveChatStreamMiddleware.subscribeLiveChatRoom(agentConnectListener);
                }
                ws.sendText(LiveChatSubRPC.streamLivechatRoom(uniqueID,roomID,enable));
            }
        });
    }

    private void subscribeTyping(final String roomID, final Boolean enable, final SubscribeListener subscribeListener, final TypingListener listener){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                if (subscribeListener !=null) {
                    liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeListener, LiveChatStreamMiddleware.SubType.NOTIFYROOM);
                }
                if (listener!=null){
                    liveChatStreamMiddleware.subscribeTyping(listener);
                }
                ws.sendText(LiveChatSubRPC.subscribeTyping(uniqueID,roomID,enable));
            }
        });
    }

    private void closeConversation(final String roomId){
        EventThread.exec(new Runnable() {
            @Override
            public void run() {
                int uniqueID = integer.getAndIncrement();
                ws.sendText(LiveChatBasicRPC.closeConversation(uniqueID,roomId));
            }
        });
    }


    public void connect(ConnectListener connectListener) {
        createSocket();
        ws.addListener(adapter);
        this.connectListener = connectListener;
        super.connectAsync();
    }

    WebSocketAdapter getAdapter() {

        return new WebSocketAdapter(){

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("On Error");
                super.onError(websocket, cause);
            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                System.out.println("Connected");
                integer.set(1);
                websocket.sendText(LiveChatBasicRPC.ConnectObject());
                super.onConnected(websocket, headers);
            }

            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                System.out.println("Disconnected");
                if (connectListener!=null) {
                    connectListener.onDisconnect(closedByServer);
                }
                super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                System.out.println("Onconnect Error");
                super.onConnectError(websocket, exception);
            }


            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                System.out.println("Message is " + text);
                JSONObject object = new JSONObject(text);
                switch (RPC.parse(object.optString("msg"))) {
                    case PING:
                        websocket.sendText("{\"msg\":\"pong\"}");
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
                super.onTextMessage(websocket, text);
            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On close frame");
                super.onCloseFrame(websocket, frame);
            }

            @Override
            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("On send error");
                super.onSendError(websocket, cause, frame);
            }
        };
    }

    public ChatRoom createRoom(String userID,String authToken){
        String userName=userInfo.optString("username");
        String visitorToken= LiveChatBasicRPC.visitorToken;
        String roomID=Utils.shortUUID();
        return new ChatRoom(userName,roomID,userID,visitorToken,authToken);
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

        public void login(AuthListener.LoginListener listener){
            LiveChatAPI.this.login(authToken,listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp,Date lasttimestamp,LoadHistoryListener listener){
            LiveChatAPI.this.getChatHistory(roomId,limit,oldestMessageTimestamp,lasttimestamp,listener);
        }

        public void getAgentData(AgentListener.AgentDataListener listener){
            LiveChatAPI.this.getAgentData(roomId,listener);
        }

        public void sendMessage(String message){
            LiveChatAPI.this.sendMessage(Utils.shortUUID(),roomId,message,visitorToken);
        }

        public void sendIsTyping(Boolean istyping){
            LiveChatAPI.this.sendIsTyping(roomId,userName,istyping);
        }

        public void subscribeRoom(SubscribeListener subscribeListener,MessageListener listener){
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
            return "ChatRoom{" +
                    "userName='" + userName + '\'' +
                    ", roomId='" + roomId + '\'' +
                    ", userId='" + userId + '\'' +
                    ", visitorToken='" + visitorToken + '\'' +
                    ", authToken='" + authToken + '\'' +
                    '}';
        }
    }

}
