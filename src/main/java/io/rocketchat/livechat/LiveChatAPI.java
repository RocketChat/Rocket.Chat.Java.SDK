package io.rocketchat.livechat;

import com.neovisionaries.ws.client.*;
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
    JSONObject userInfo;

    WebSocketListener listener;
    LiveChatMiddleware liveChatMiddleware;
    LiveChatStreamMiddleware liveChatStreamMiddleware;

    ConnectCallback connectCallback;


    public LiveChatAPI(String url) {
        super(url);
        listener=getListener();
        integer=new AtomicInteger(1);
        liveChatMiddleware =LiveChatMiddleware.getInstance();
        liveChatStreamMiddleware=LiveChatStreamMiddleware.getInstance();
    }

    public void getInitialData(final InitialDataCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,callback, LiveChatMiddleware.CallbackType.GETINITIALDATA);
                ws.sendText(LiveChatBasicRPC.getInitialData(uniqueID));
            }
        });
    }

    public void registerGuest(final String name, final String email, final String dept, final GuestCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,callback, LiveChatMiddleware.CallbackType.REGISTERORLOGIN);
                ws.sendText(LiveChatBasicRPC.registerGuest(uniqueID,name,email,dept));
            }
        });
    }

    public void login(final String token, final GuestCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID=integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,callback, LiveChatMiddleware.CallbackType.REGISTERORLOGIN);
                ws.sendText(LiveChatBasicRPC.login(uniqueID,token));
            }
        });
    }


    public void getChatHistory(final String roomID, final int limit, final Date lasttimestamp, final HistoryCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID = integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,callback, LiveChatMiddleware.CallbackType.GETCHATHISTORY);
                ws.sendText(LiveChatHistoryRPC.loadHistory(uniqueID,roomID,limit,lasttimestamp));
            }
        });

    }


    public void getAgentData(final String roomId, final AgentCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID = integer.getAndIncrement();
                liveChatMiddleware.createCallback(uniqueID,callback, LiveChatMiddleware.CallbackType.GETAGENTDATA);
                ws.sendText(LiveChatBasicRPC.getAgentData(uniqueID,roomId));
            }
        });
    }


    public void sendMessage(final String msgId, final String roomID, final String message, final String token){
        EventThread.exec(new Runnable() {
            public void run() {
                int uniqueID = integer.getAndIncrement();
                ws.sendText(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
            }
        });
    }

    public void sendIsTyping(String roomId, String username, Boolean istyping){
                int uniqueID = integer.getAndIncrement();
                ws.sendText(LiveChatTypingRPC.streamNotifyRoom(uniqueID,roomId,username,istyping));
    }


    public void subscribeRoom(final String roomID, final Boolean enable){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                ws.sendText(LiveChatSubRPC.streamRoomMessages(uniqueID,roomID,enable));
            }
        });
    }

    public void subscribeLiveChatRoom(final String roomID, final Boolean enable){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                ws.sendText(LiveChatSubRPC.streamLivechatRoom(uniqueID,roomID,enable));
            }
        });
    }

    public void subscribeTyping(final String roomID, final Boolean enable){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                ws.sendText(LiveChatSubRPC.subscribeTyping(uniqueID,roomID,enable));
            }
        });
    }

    public void subscribeRoom(final String roomID, final Boolean enable, final SubscribeCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                liveChatStreamMiddleware.createSubCallbacks(uniqueID,callback, LiveChatStreamMiddleware.subscriptiontype.STREAMROOMMESSAGES);
                ws.sendText(LiveChatSubRPC.streamRoomMessages(uniqueID,roomID,enable));
            }
        });
    }

    public void subscribeLiveChatRoom(final String roomID, final Boolean enable, final SubscribeCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                liveChatStreamMiddleware.createSubCallbacks(uniqueID,callback, LiveChatStreamMiddleware.subscriptiontype.STREAMLIVECHATROOM);
                ws.sendText(LiveChatSubRPC.streamLivechatRoom(uniqueID,roomID,enable));
            }
        });
    }

    public void subscribeTyping(final String roomID, final Boolean enable, final SubscribeCallback callback){
        EventThread.exec(new Runnable() {
            public void run() {
                String uniqueID=Utils.shortUUID();
                liveChatStreamMiddleware.createSubCallbacks(uniqueID,callback, LiveChatStreamMiddleware.subscriptiontype.NOTIFYROOM);
                ws.sendText(LiveChatSubRPC.subscribeTyping(uniqueID,roomID,enable));
            }
        });
    }

    public void connect(){
        createWebsocketfactory();
        ws.addListener(listener);
        super.connect();
    }

    public void connectAsync(ConnectCallback connectCallback) {
        createWebsocketfactory();
        ws.addListener(listener);
        this.connectCallback=connectCallback;
        super.connectAsync();
    }

    WebSocketListener getListener() {
        return new WebSocketListener() {
            public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
//                System.out.println("on state changed");
            }

            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                integer.set(1);
                websocket.sendText(LiveChatBasicRPC.ConnectObject());
                System.out.println("Connected to server");
            }

            public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("got connect error");
            }

            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                System.out.println("Disconnected to server");
            }

            public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("Got frame");
            }

            public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on continuation frame");
            }

            public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                //   System.out.println("On text frame");
            }

            public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on binary frame");
            }

            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On close frame");
            }

            public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On ping frame" + frame.getPayloadText());
            }

            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on pong frame");
            }

            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                System.out.println("Message is " + text);

                JSONObject object = new JSONObject(text);
                if (object.optString("msg").equals("ping")) {
                    websocket.sendText("{\"msg\":\"pong\"}");
                } else if (object.optString("msg").equals("connected")) {
                    sessionId = object.optString("session");
                    if (connectCallback != null) {
                        connectCallback.onConnect(sessionId);
                    }
                } else if (object.optString("msg").equals("added")){
                    if (object.optString("collection")!=null && object.optString("collection").equals("users")) {
                        userInfo = object.optJSONObject("fields");
                    }
                }else if (Utils.isInteger(object.optString("id"))) {
                    liveChatMiddleware.processCallback(Long.valueOf(object.optString("id")), object);
                }else if (object.optString("msg").equals("ready")){
                    liveChatStreamMiddleware.processSubSuccess(object);
                }else if (object.optString("msg").equals("changed")){
                    liveChatStreamMiddleware.processCallback(object);
                }
            }

            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                System.out.println("on binary message");
            }

            public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("on sending frame");
            }

            public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("on frame set "+frame.getPayloadText());
            }

            public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on frame unsent");
            }

            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("On error");
            }

            public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("On frame error");
            }

            public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                System.out.println("On message error");
            }

            public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
                System.out.println("on message decompression error");
            }

            public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                System.out.println("on text message error");
            }

            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("on send error");
            }

            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("on unexpected error");
            }

            public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
                System.out.println("handle callback error");
            }

            public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
//                System.out.println("On sending handshake");
            }
        };


    }
}
