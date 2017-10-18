package com.rocketchat.livechat;

import com.rocketchat.common.SocketListener;
import com.rocketchat.common.data.CommonJsonAdapterFactory;
import com.rocketchat.common.data.TimestampAdapter;
import com.rocketchat.common.data.model.MessageType;
import com.rocketchat.common.data.model.internal.SocketMessage;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SubscribeCallback;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.network.SocketFactory;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.NoopLogger;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.livechat.callback.AgentCallback;
import com.rocketchat.livechat.callback.AuthCallback;
import com.rocketchat.livechat.callback.InitialDataCallback;
import com.rocketchat.livechat.callback.LoadHistoryCallback;
import com.rocketchat.livechat.callback.MessageListener;
import com.rocketchat.livechat.internal.middleware.LiveChatMiddleware;
import com.rocketchat.livechat.internal.middleware.LiveChatStreamMiddleware;
import com.rocketchat.livechat.internal.rpc.LiveChatBasicRPC;
import com.rocketchat.livechat.internal.rpc.LiveChatHistoryRPC;
import com.rocketchat.livechat.internal.rpc.LiveChatSendMsgRPC;
import com.rocketchat.livechat.internal.rpc.LiveChatSubRPC;
import com.rocketchat.livechat.internal.rpc.LiveChatTypingRPC;
import com.rocketchat.livechat.model.JsonAdapterFactory;
import com.squareup.moshi.Moshi;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.OkHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import static com.rocketchat.common.utils.Preconditions.checkNotNull;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 30/7/17 Make it singletone like eventbus, add builder class to LiveChatAPI in order to use it anywhere
public class LiveChatClient implements SocketListener {

    private final Logger logger;
    private final Socket socket;

    private AtomicInteger integer;
    private String sessionId;
    private JSONObject userInfo;

    private ConnectListener connectListener;

    private LiveChatMiddleware liveChatMiddleware;
    private LiveChatStreamMiddleware liveChatStreamMiddleware;

    public LiveChatClient(Builder builder) {
        OkHttpClient client;
        if (builder.client == null) {
            client = new OkHttpClient();
        } else {
            client = builder.client;
        }

        SocketFactory factory;
        if (builder.factory != null) {
            factory = builder.factory;
        } else {
            factory = new SocketFactory() {
                @Override
                public Socket create(OkHttpClient client, String url, Logger logger, SocketListener socketListener) {
                    return new Socket(client, url, logger, socketListener);
                }
            };
        }

        if (builder.logger != null) {
            this.logger = builder.logger;
        } else {
            this.logger = new NoopLogger();
        }

        // TODO - Add to the Builder
        Moshi moshi = new Moshi.Builder()
                .add(new TimestampAdapter())
                .add(JsonAdapterFactory.create())
                .add(CommonJsonAdapterFactory.create())
                .build();

        socket = factory.create(client, builder.websocketUrl, logger, this);

        integer = new AtomicInteger(1);
        liveChatMiddleware = new LiveChatMiddleware(moshi);
        liveChatStreamMiddleware = new LiveChatStreamMiddleware(moshi);
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void getInitialData(InitialDataCallback listener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, listener, LiveChatMiddleware.CallbackType.GET_INITIAL_DATA);
        socket.sendData(LiveChatBasicRPC.getInitialData(uniqueID));
    }

    public void registerGuest(String name, String email, String dept, AuthCallback.RegisterCallback listener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, listener, LiveChatMiddleware.CallbackType.REGISTER);
        socket.sendData(LiveChatBasicRPC.registerGuest(uniqueID, name, email, dept));
    }

    public void login(String token, AuthCallback.LoginCallback listener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, listener, LiveChatMiddleware.CallbackType.LOGIN);
        socket.sendData(LiveChatBasicRPC.login(uniqueID, token));
    }

    public void sendOfflineMessage(String name, String email, String message) {
        int uniqueID = integer.getAndIncrement();
        socket.sendData(LiveChatBasicRPC.sendOfflineMessage(uniqueID, name, email, message));
    }

    public void sendOfflineMessage(String name, String email, String message,
                                   MessageListener.OfflineMessageCallback listener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, listener, LiveChatMiddleware.CallbackType.SEND_OFFLINE_MESSAGE);
        socket.sendData(LiveChatBasicRPC.sendOfflineMessage(uniqueID, name, email, message));
    }

    private void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp, Date lasttimestamp,
                                LoadHistoryCallback listener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, listener, LiveChatMiddleware.CallbackType.GET_CHAT_HISTORY);
        socket.sendData(
                LiveChatHistoryRPC.loadHistory(uniqueID, roomID, oldestMessageTimestamp, limit, lasttimestamp));
    }

    private void getAgentData(String roomId, AgentCallback.AgentDataCallback listener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, listener, LiveChatMiddleware.CallbackType.GET_AGENT_DATA);
        socket.sendData(LiveChatBasicRPC.getAgentData(uniqueID, roomId));
    }

    private void sendMessage(String msgId, String roomID, String message, String token) {
        int uniqueID = integer.getAndIncrement();
        socket.sendData(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
    }

    private void sendMessage(String msgId, String roomID, String message, String token,
                             MessageListener.MessageAckCallback messageAckListener) {
        int uniqueID = integer.getAndIncrement();
        liveChatMiddleware.createCallback(uniqueID, messageAckListener, LiveChatMiddleware.CallbackType.SEND_MESSAGE);
        socket.sendData(LiveChatSendMsgRPC.sendMessage(uniqueID, msgId, roomID, message, token));
    }

    private void sendIsTyping(String roomId, String username, Boolean istyping) {
        int uniqueID = integer.getAndIncrement();
        socket.sendData(LiveChatTypingRPC.streamNotifyRoom(uniqueID, roomId, username, istyping));
    }

    private void subscribeRoom(String roomID, Boolean enable, SubscribeCallback subscribeCallback,
                               MessageListener.SubscriptionListener listener) {

        String uniqueID = Utils.shortUUID();
        liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeCallback);
        liveChatStreamMiddleware.subscribeRoom(listener);
        socket.sendData(LiveChatSubRPC.streamRoomMessages(uniqueID, roomID, enable));
    }

    private void subscribeLiveChatRoom(String roomID, Boolean enable, SubscribeCallback subscribeCallback,
                                       AgentCallback.AgentConnectListener agentConnectListener) {

        String uniqueID = Utils.shortUUID();
        liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeCallback);
        liveChatStreamMiddleware.subscribeLiveChatRoom(agentConnectListener);
        socket.sendData(LiveChatSubRPC.streamLivechatRoom(uniqueID, roomID, enable));
    }

    private void subscribeTyping(String roomID, Boolean enable, SubscribeCallback subscribeCallback,
                                 TypingListener listener) {

        String uniqueID = Utils.shortUUID();
        liveChatStreamMiddleware.createSubCallbacks(uniqueID, subscribeCallback);
        liveChatStreamMiddleware.subscribeTyping(listener);
        socket.sendData(LiveChatSubRPC.subscribeTyping(uniqueID, roomID, enable));
    }

    private void closeConversation(String roomId) {
        int uniqueID = integer.getAndIncrement();
        socket.sendData(LiveChatBasicRPC.closeConversation(uniqueID, roomId));
    }

    public void connect(ConnectListener connectListener) {
        socket.connect();
        this.connectListener = connectListener;
    }

    @Override
    public void onConnected() {
        logger.info("LiveChatClient Connected");
        integer.set(1);
        socket.sendData(LiveChatBasicRPC.ConnectObject());
    }

    @Override
    public void onMessageReceived(MessageType type, /* nullable */ String id, String message) {
        /* FIXME - temporary JSONObject while we don't convert everything to Moshi and AutoValue */
        JSONObject object = null;
        try {
            object = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        switch (type) {
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
        }
    }

    @Override
    public void onClosing() {
        logger.info("onClosing");
    }

    @Override
    public void onClosed() {
        logger.info("onClosed");
        liveChatMiddleware.cleanup();
        if (connectListener != null) {
            connectListener.onDisconnect(true);
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        liveChatMiddleware.notifyDisconnection(throwable.getMessage());
        if (connectListener != null) {
            connectListener.onConnectError(throwable);
        }
    }

    public ChatRoom createRoom(String userID, String authToken) {
        String userName = null;
        if (userInfo != null) {
            userName = userInfo.optString("username");
        }
        String visitorToken = LiveChatBasicRPC.visitorToken;
        String roomID = Utils.shortUUID();
        return new ChatRoom(userName, roomID, userID, visitorToken, authToken);
    }

    public ChatRoom createRoom(String s) {
        return new ChatRoom(s);
    }

    public class ChatRoom {

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

        public ChatRoom(String s) {
            try {
                JSONObject object = new JSONObject(s);
                this.userName = object.getString("userName");
                this.roomId = object.getString("roomId");
                this.userId = object.getString("userId");
                this.visitorToken = object.getString("visitorToken");
                this.authToken = object.getString("authToken");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void login(AuthCallback.LoginCallback listener) {
            LiveChatClient.this.login(authToken, listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp,
                                   LoadHistoryCallback listener) {
            LiveChatClient.this.getChatHistory(roomId, limit, oldestMessageTimestamp, lasttimestamp, listener);
        }

        public void getAgentData(AgentCallback.AgentDataCallback listener) {
            LiveChatClient.this.getAgentData(roomId, listener);
        }

        /**
         * Used for sending messages to server
         *
         * @param message to be sent
         * @return MessageID
         */
        public String sendMessage(String message) {
            String uuid = Utils.shortUUID();
            LiveChatClient.this.sendMessage(uuid, roomId, message, visitorToken);
            return uuid;
        }

        /**
         * Used for sending messages to server with messageAcknowledgement
         *
         * @param messageAckListener Returns ack to particular message
         * @return MessageID
         */

        public String sendMessage(String message, MessageListener.MessageAckCallback messageAckListener) {
            String uuid = Utils.shortUUID();
            LiveChatClient.this.sendMessage(uuid, roomId, message, visitorToken, messageAckListener);
            return uuid;
        }

        public void sendIsTyping(Boolean istyping) {
            LiveChatClient.this.sendIsTyping(roomId, userName, istyping);
        }

        public void subscribeRoom(SubscribeCallback subscribeCallback, MessageListener.SubscriptionListener listener) {
            LiveChatClient.this.subscribeRoom(roomId, false, subscribeCallback, listener);
        }

        public void subscribeLiveChatRoom(SubscribeCallback subscribeCallback,
                                          AgentCallback.AgentConnectListener agentConnectListener) {
            LiveChatClient.this.subscribeLiveChatRoom(roomId, false, subscribeCallback, agentConnectListener);
        }

        public void subscribeTyping(SubscribeCallback subscribeCallback, TypingListener listener) {
            LiveChatClient.this.subscribeTyping(roomId, false, subscribeCallback, listener);
        }

        public void closeConversation() {
            LiveChatClient.this.closeConversation(roomId);
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

    public static final class Builder {
        private String websocketUrl;
        private OkHttpClient client;
        private SocketFactory factory;
        private Logger logger;

        public Builder websocketUrl(String url) {
            this.websocketUrl = checkNotNull(url, "url == null");
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = checkNotNull(client, "client must be non null");
            return this;
        }

        public Builder socketFactory(SocketFactory factory) {
            this.factory = checkNotNull(factory, "factory == null");
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = checkNotNull(logger, "logger == null");
            return this;
        }

        public LiveChatClient build() {
            return new LiveChatClient(this);
        }
    }
}
