package com.rocketchat.core;

import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.SocketListener;
import com.rocketchat.common.data.lightdb.DbManager;
import com.rocketchat.common.data.model.Room;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.network.ConnectivityManager;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.network.SocketFactory;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.callback.RoomCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.middleware.CoreMiddleware;
import com.rocketchat.core.middleware.CoreStreamMiddleware;
import com.rocketchat.core.model.Emoji;
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.PublicSetting;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.RoomObject;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import com.rocketchat.core.provider.TokenProvider;
import com.rocketchat.core.rpc.AccountRPC;
import com.rocketchat.core.rpc.BasicRPC;
import com.rocketchat.core.rpc.ChatHistoryRPC;
import com.rocketchat.core.rpc.CoreSubRPC;
import com.rocketchat.core.rpc.FileUploadRPC;
import com.rocketchat.core.rpc.MessageRPC;
import com.rocketchat.core.rpc.PresenceRPC;
import com.rocketchat.core.rpc.RoomRPC;
import com.rocketchat.core.rpc.TypingRPC;
import com.rocketchat.core.uploader.FileUploader;
import com.rocketchat.core.uploader.IFileUpload;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static com.rocketchat.common.utils.Utils.checkNotNull;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 30/7/17 Make it singletone like eventbus, add builder class to RocketChatAPI in order to use it anywhere, maybe a common builder class
public class RocketChatAPI implements SocketListener {

    public static final Logger LOGGER = Logger.getLogger(RocketChatAPI.class.getName());

    private final HttpUrl baseUrl;
    private final OkHttpClient client;

    private AtomicInteger integer;
    private String sessionId;
    private String userId;

    private CoreMiddleware coreMiddleware;
    private CoreStreamMiddleware coreStreamMiddleware;
    private DbManager dbManager;
    private Socket socket;
    private TokenProvider tokenProvider;
    private RestHelper restHelper;

    private ConnectivityManager connectivityManager;

    // chatRoomFactory class
    private ChatRoomFactory chatRoomFactory;

    private RocketChatAPI(final Builder builder) {
        if (builder.baseUrl == null || builder.websocketUrl == null) {
            throw new IllegalStateException("You must provide both restBaseUrl and websocketUrl");
        }
        this.baseUrl = builder.baseUrl;

        if (builder.client == null) {
            client = new OkHttpClient();
        } else {
            client = builder.client;
        }

        if (builder.factory != null) {
            this.socket = builder.factory.create(client, builder.websocketUrl, this);
        } else {
            this.socket = new SocketFactory() {
                @Override
                public Socket create(OkHttpClient client, String url, SocketListener socketListener) {
                    return new Socket(client, url, socketListener);
                }
            }.create(client, builder.websocketUrl, this);
        }

        tokenProvider = builder.provider;

        integer = new AtomicInteger(1);
        coreMiddleware = new CoreMiddleware();
        coreStreamMiddleware = new CoreStreamMiddleware();
        dbManager = new DbManager();
        chatRoomFactory = new ChatRoomFactory(this);

        connectivityManager = new ConnectivityManager();

        restHelper = new RestHelper(client, baseUrl, tokenProvider);
    }

    public String getMyUserName() {
        return dbManager.getUserCollection().get(userId).getUserName();
    }

    public String getMyUserId() {
        return userId;
    }

    public ChatRoomFactory getChatRoomFactory() {
        return chatRoomFactory;
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    public void signin(String username, String password, final LoginCallback loginCallback) {
        restHelper.signin(username, password, loginCallback);
    }

    public void pinMessage(String messageId, SimpleCallback callback) {
        restHelper.pinMessage(messageId, callback);
    }

    public void login(LoginCallback loginCallback) {
        TokenObject token = tokenProvider != null ? tokenProvider.getToken() : null;
        if (token == null) {
            loginCallback.onError(new RocketChatAuthException("Missing token"));
            return;
        }

        loginUsingToken(token.getAuthToken(), loginCallback);
    }

    //Tested
    public void login(String username, String password, LoginCallback loginCallback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        socket.sendData(BasicRPC.login(uniqueID, username, password));
    }

    //Tested
    public void loginUsingToken(String token, LoginCallback loginCallback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        socket.sendData(BasicRPC.loginUsingToken(uniqueID, token));
    }

    //Tested
    public void getPermissions(SimpleListCallback<Permission> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_PERMISSIONS);
        socket.sendData(AccountRPC.getPermissions(uniqueID, null));
    }

    //Tested
    public void getPublicSettings(SimpleListCallback<PublicSetting> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_PUBLIC_SETTINGS);
        socket.sendData(AccountRPC.getPublicSettings(uniqueID, null));
    }

    //Tested
    public void getUserRoles(SimpleListCallback<UserObject> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_USER_ROLES);
        socket.sendData(BasicRPC.getUserRoles(uniqueID));
    }

    //Tested
    public void listCustomEmoji(SimpleListCallback<Emoji> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.LIST_CUSTOM_EMOJI);
        socket.sendData(BasicRPC.listCustomEmoji(uniqueID));
    }

    //Tested
    public void logout(SimpleCallback listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.CallbackType.LOGOUT);
        socket.sendData(BasicRPC.logout(uniqueID));
    }

    //Tested
    public void getSubscriptions(SimpleListCallback<SubscriptionObject> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_SUBSCRIPTIONS);
        socket.sendData(BasicRPC.getSubscriptions(uniqueID));
    }

    //Tested
    public void getRooms(SimpleListCallback<RoomObject> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_ROOMS);
        socket.sendData(BasicRPC.getRooms(uniqueID));
    }

    //Tested
    private void getRoomRoles(String roomId, SimpleListCallback<RoomRole> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_ROOM_ROLES);
        socket.sendData(BasicRPC.getRoomRoles(uniqueID, roomId));
    }

    //Tested
    private void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp,
                                Date lasttimestamp, HistoryCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.LOAD_HISTORY);
        socket.sendData(ChatHistoryRPC.loadHistory(uniqueID, roomID, oldestMessageTimestamp, limit, lasttimestamp));
    }

    private void getRoomMembers(String roomID, Boolean allUsers, RoomCallback.GetMembersCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.GET_ROOM_MEMBERS);
        socket.sendData(RoomRPC.getRoomMembers(uniqueID, roomID, allUsers));
    }

    //Tested
    private void sendIsTyping(String roomId, String username, Boolean istyping) {
        int uniqueID = integer.getAndIncrement();
        socket.sendData(TypingRPC.sendTyping(uniqueID, roomId, username, istyping));
    }

    //Tested
    private void sendMessage(String msgId, String roomID, String message, MessageCallback.MessageAckCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.SEND_MESSAGE);
        socket.sendData(MessageRPC.sendMessage(uniqueID, msgId, roomID, message));
    }

    //Tested
    private void deleteMessage(String msgId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.MESSAGE_OP);
        socket.sendData(MessageRPC.deleteMessage(uniqueID, msgId));
    }

    //Tested
    private void updateMessage(String msgId, String roomId, String message, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.MESSAGE_OP);
        socket.sendData(MessageRPC.updateMessage(uniqueID, msgId, roomId, message));
    }

    //Tested
    private void pinMessage(JSONObject message, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.MESSAGE_OP);
        socket.sendData(MessageRPC.pinMessage(uniqueID, message));
    }

    //Tested
    private void unpinMessage(JSONObject message, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.MESSAGE_OP);
        socket.sendData(MessageRPC.unpinMessage(uniqueID, message));
    }

    //Tested
    private void starMessage(String msgId, String roomId, Boolean starred, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.MESSAGE_OP);
        socket.sendData(MessageRPC.starMessage(uniqueID, msgId, roomId, starred));
    }

    //Tested
    private void setReaction(String emojiId, String msgId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.MESSAGE_OP);
        socket.sendData(MessageRPC.setReaction(uniqueID, emojiId, msgId));
    }

    private void searchMessage(String message, String roomId, int limit, 
                               SimpleListCallback<RocketChatMessage> callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.SEARCH_MESSAGE);
        socket.sendData(MessageRPC.searchMessage(uniqueID, message, roomId, limit));
    }

    //Tested
    public void createPublicGroup(String groupName, String[] users, Boolean readOnly,
                                  RoomCallback.GroupCreateCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.CREATE_GROUP);
        socket.sendData(RoomRPC.createPublicGroup(uniqueID, groupName, users, readOnly));
    }

    //Tested
    public void createPrivateGroup(String groupName, String[] users,
                                   RoomCallback.GroupCreateCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.CREATE_GROUP);
        socket.sendData(RoomRPC.createPrivateGroup(uniqueID, groupName, users));
    }

    //Tested
    private void deleteGroup(String roomId, SimpleCallback callback) {
        //Apply simpleListener
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.DELETE_GROUP);
        socket.sendData(RoomRPC.deleteGroup(uniqueID, roomId));
    }

    //Tested
    private void archiveRoom(String roomId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.ARCHIVE);
        socket.sendData(RoomRPC.archieveRoom(uniqueID, roomId));
    }

    //Tested
    private void unarchiveRoom(String roomId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.UNARCHIVE);
        socket.sendData(RoomRPC.unarchiveRoom(uniqueID, roomId));
    }

    //Tested
    public void joinPublicGroup(String roomId, String joinCode, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.JOIN_PUBLIC_GROUP);
        socket.sendData(RoomRPC.joinPublicGroup(uniqueID, roomId, joinCode));
    }

    //Tested
    private void leaveGroup(String roomId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.LEAVE_GROUP);
        socket.sendData(RoomRPC.leaveGroup(uniqueID, roomId));
    }

    //Tested
    private void hideRoom(String roomId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.HIDE_ROOM);
        socket.sendData(RoomRPC.hideRoom(uniqueID, roomId));
    }

    //Tested
    private void openRoom(String roomId, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.OPEN_ROOM);
        socket.sendData(RoomRPC.openRoom(uniqueID, roomId));
    }

    //Tested
    private void setFavouriteRoom(String roomId, Boolean isFavouriteRoom, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.SET_FAVOURITE_ROOM);
        socket.sendData(RoomRPC.setFavouriteRoom(uniqueID, roomId, isFavouriteRoom));
    }

    private void sendFileMessage(String roomId, String store, String fileId, String fileType,
                                 int size, String fileName, String desc, String url,
                                 MessageCallback.MessageAckCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.SEND_MESSAGE);
        socket.sendData(MessageRPC.sendFileMessage(uniqueID, roomId, store, fileId, fileType, size, fileName, desc, url));
    }

    //Tested
    public void setStatus(UserObject.Status s, SimpleCallback callback) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, callback, CoreMiddleware.CallbackType.SET_STATUS);
        socket.sendData(PresenceRPC.setDefaultStatus(uniqueID, s));
    }

    public void subscribeActiveUsers(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubscriptionListener(uniqueID, subscribeListener);
        socket.sendData(CoreSubRPC.subscribeActiveUsers(uniqueID));
    }

    public void subscribeUserData(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubscriptionListener(uniqueID, subscribeListener);
        socket.sendData(CoreSubRPC.subscribeUserData(uniqueID));
    }

    //Tested
    private String subscribeRoomMessageEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, MessageCallback.SubscriptionCallback listener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubscriptionListener(uniqueID, subscribeListener);
        coreStreamMiddleware.createSubscription(roomId, listener, CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_MESSAGE);
        socket.sendData(CoreSubRPC.subscribeRoomMessageEvent(uniqueID, roomId, enable));
        return uniqueID;
    }

    private String subscribeRoomTypingEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, TypingListener listener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubscriptionListener(uniqueID, subscribeListener);
        coreStreamMiddleware.createSubscription(roomId, listener, CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_TYPING);
        socket.sendData(CoreSubRPC.subscribeRoomTypingEvent(uniqueID, roomId, enable));
        return uniqueID;
    }

    private void unsubscribeRoom(String subId, SubscribeListener subscribeListener) {
        socket.sendData(CoreSubRPC.unsubscribeRoom(subId));
        coreStreamMiddleware.createSubscriptionListener(subId, subscribeListener);
    }

    public void connect(ConnectListener connectListener) {
        connectivityManager.register(connectListener);
        socket.connect();
    }

    public void disconnect() {
        socket.disconnect();
    }

    @Override
    public void onConnected() {
        LOGGER.info("RocketChatAPI Connected");
        integer.set(1);
        socket.sendData(BasicRPC.ConnectObject());
    }

    @Override
    public void onMessageReceived(JSONObject message) {
        switch (RPC.getMessageType(message.optString("msg"))) {
            case CONNECTED:
                processOnConnected(message);
                break;
            case RESULT:
                coreMiddleware.processCallback(Long.valueOf(message.optString("id")), message);
                break;
            case READY:
                coreStreamMiddleware.processSubscriptionSuccess(message);
                break;
            case ADDED:
                processCollectionsAdded(message);
                break;
            case CHANGED:
                processCollectionsChanged(message);
                break;
            case REMOVED:
                dbManager.update(message, RPC.MsgType.REMOVED);
                break;
            case NOSUB:
                coreStreamMiddleware.processUnsubscriptionSuccess(message);
                break;
            case OTHER:
                break;
            default:

                break;
        }
    }

    @Override
    public void onClosing() {
        LOGGER.info("onClosing");
    }

    @Override
    public void onClosed() {
        LOGGER.info("onClosed");
        coreMiddleware.cleanup();
        connectivityManager.publishDisconnect(true);
    }

    @Override
    public void onFailure(Throwable throwable) {
        LOGGER.info("onFailure: " + throwable);
        coreMiddleware.notifyDisconnection(throwable.getMessage());
        connectivityManager.publishConnectError(throwable);
    }

    private void sendPingFrames() {
        /*if (isPingEnabled()) {
            reschedulePing();
        }*/
    }

    public void setReconnectionStrategy(ReconnectionStrategy strategy) {
        socket.setReconnectionStrategy(strategy);
    }

    public void setPingInterval(long interval) {
        socket.setPingInterval(interval);
    }

    public void disablePing() {
        socket.disablePing();
    }

    public void enablePing() {
        socket.enablePing();
    }

    private void processOnConnected(JSONObject object) {
        sessionId = object.optString("session");
        connectivityManager.publishConnect(sessionId);
        /*sendData(BasicRPC.PING_MESSAGE);*/
    }

    private void processCollectionsAdded(JSONObject object) {
        if (userId == null) {
            userId = object.optString("id");
        }
        dbManager.update(object, RPC.MsgType.ADDED);
    }

    private void processCollectionsChanged(JSONObject object) {
        switch (DbManager.getCollectionType(object)) {
            case STREAM_COLLECTION:
                coreStreamMiddleware.processListeners(object);
                break;
            case COLLECTION:
                dbManager.update(object, RPC.MsgType.CHANGED);
                break;
        }
    }

    public void createUFS(String fileName, int fileSize, String fileType, String roomId, String description, String store, IFileUpload.UfsCreateCallback listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.CallbackType.UFS_CREATE);
        socket.sendData(FileUploadRPC.ufsCreate(uniqueID, fileName, fileSize, fileType, roomId, description, store));
    }

    public void completeUFS(String fileId, String store, String token, IFileUpload.UfsCompleteListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.CallbackType.UFS_COMPLETE);
        socket.sendData(FileUploadRPC.ufsComplete(uniqueID, fileId, store, token));
    }

    /**
     * ChatRoom class to access private methods
     */

    // TODO: 29/7/17 add throw custom exceptions if method call violates permission required to execute given RPC
    public class ChatRoom {

        Room room;

        //Subscription Ids for new subscriptions
        private String roomSubId;  // TODO: 29/7/17 check for persistent SubscriptionId of the room
        private String typingSubId;

        public ChatRoom(Room room) {
            this.room = room;
        }

        public Boolean isSubscriptionObject() {
            return room instanceof SubscriptionObject;
        }

        public Room getRoomData() {
            return room;
        }

        //RPC methods

        public void getRoomRoles(SimpleListCallback<RoomRole> callback) {
            RocketChatAPI.this.getRoomRoles(room.getRoomId(), callback);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp,
                                   HistoryCallback callback) {
            RocketChatAPI.this.getChatHistory(room.getRoomId(), limit, oldestMessageTimestamp, lasttimestamp, callback);
        }

        public void getMembers(RoomCallback.GetMembersCallback callback) {
            RocketChatAPI.this.getRoomMembers(room.getRoomId(), false, callback);
        }

        public void sendIsTyping(Boolean istyping) {
            RocketChatAPI.this.sendIsTyping(room.getRoomId(), getMyUserName(), istyping);
        }

        public void sendMessage(String message) {
            RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message, null);
        }

        public void sendMessage(String message, MessageCallback.MessageAckCallback callback) {
            RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message, callback);
        }

        // TODO: 27/7/17 Need more attention on replying to message
        private void replyMessage(RocketChatMessage msg, String message,
                                  MessageCallback.MessageAckCallback callback) {
            message = "[ ](?msg=" + msg.getMessageId() + ") @" + msg.getSender().getUserName() + " " + message;
            RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message, callback);
        }

        public void deleteMessage(String msgId, SimpleCallback callback) {
            RocketChatAPI.this.deleteMessage(msgId, callback);
        }

        public void updateMessage(String msgId, String message, SimpleCallback callback) {
            RocketChatAPI.this.updateMessage(msgId, room.getRoomId(), message, callback);
        }

        public void pinMessage(JSONObject message, SimpleCallback callback) {
            RocketChatAPI.this.pinMessage(message, callback);
        }

        public void unpinMessage(JSONObject message, SimpleCallback callback) {
            RocketChatAPI.this.unpinMessage(message, callback);
        }

        public void starMessage(String msgId, Boolean starred, SimpleCallback callback) {
            RocketChatAPI.this.starMessage(msgId, room.getRoomId(), starred, callback);
        }

        public void setReaction(String emojiId, String msgId, SimpleCallback callback) {
            RocketChatAPI.this.setReaction(emojiId, msgId, callback);
        }

        public void searchMessage(String message, int limit,
                                  SimpleListCallback<RocketChatMessage> callback) {
            RocketChatAPI.this.searchMessage(message, room.getRoomId(), limit, callback);
        }

        public void deleteGroup(SimpleCallback callback) {
            RocketChatAPI.this.deleteGroup(room.getRoomId(), callback);
        }

        public void archive(SimpleCallback callback) {
            RocketChatAPI.this.archiveRoom(room.getRoomId(), callback);
        }

        public void unarchive(SimpleCallback callback) {
            RocketChatAPI.this.unarchiveRoom(room.getRoomId(), callback);
        }

        public void leave(SimpleCallback callback) {
            RocketChatAPI.this.leaveGroup(room.getRoomId(), callback);
        }

        public void hide(SimpleCallback callback) {
            RocketChatAPI.this.hideRoom(room.getRoomId(), callback);
        }

        public void open(SimpleCallback callback) {
            RocketChatAPI.this.openRoom(room.getRoomId(), callback);
        }

        public void uploadFile(File file, String newName, String description, FileListener fileListener) {
            FileUploader uploader = new FileUploader(RocketChatAPI.this, file, newName, description,
                    this, fileListener);
            uploader.startUpload();
        }

        public void sendFileMessage(FileObject file, MessageCallback.MessageAckCallback callback) {
            RocketChatAPI.this.sendFileMessage(room.getRoomId(), file.getStore(), file.getFileId(),
                    file.getFileType(), file.getSize(), file.getFileName(), file.getDescription(),
                    file.getUrl(), callback);
        }

        public void setFavourite(Boolean isFavoutite, SimpleCallback callback) {
            RocketChatAPI.this.setFavouriteRoom(room.getRoomId(), isFavoutite, callback);
        }

        //Subscription methods

        public void subscribeRoomMessageEvent(SubscribeListener subscribeListener,
                                              MessageCallback.SubscriptionCallback callback) {
            if (roomSubId == null) {
                roomSubId = RocketChatAPI.this.subscribeRoomMessageEvent(room.getRoomId(),
                        true, subscribeListener, callback);
            }
        }

        public void subscribeRoomTypingEvent(SubscribeListener subscribeListener, TypingListener listener) {
            if (typingSubId == null) {
                typingSubId = RocketChatAPI.this.subscribeRoomTypingEvent(room.getRoomId(), true, subscribeListener, listener);
            }
        }

        public void unSubscribeRoomMessageEvent(SubscribeListener subscribeListener) {
            if (roomSubId != null) {
                coreStreamMiddleware.removeSubscription(room.getRoomId(), CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_MESSAGE);
                RocketChatAPI.this.unsubscribeRoom(roomSubId, subscribeListener);
                roomSubId = null;
            }
        }

        public void unSubscribeRoomTypingEvent(SubscribeListener subscribeListener) {
            if (typingSubId != null) {
                coreStreamMiddleware.removeSubscription(room.getRoomId(), CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_TYPING);
                RocketChatAPI.this.unsubscribeRoom(typingSubId, subscribeListener);
                typingSubId = null;
            }
        }

        public void unSubscribeAllEvents() {
            coreStreamMiddleware.removeAllSubscriptions(room.getRoomId());
            unSubscribeRoomMessageEvent(null);
            unSubscribeRoomTypingEvent(null);
        }

        // TODO: 29/7/17 refresh methods to be added, changing data should change internal data, maintain state of the room
    }

    public static final class Builder {
        private String websocketUrl;
        private HttpUrl baseUrl;
        private OkHttpClient client;
        private SocketFactory factory;
        private TokenProvider provider;

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

        public Builder restBaseUrl(String url) {
            checkNotNull(url, "url == null");
            HttpUrl httpUrl = HttpUrl.parse(url);
            if (httpUrl == null) {
                throw new IllegalArgumentException("Illegal URL: " + url);
            }
            return restBaseUrl(httpUrl);
        }

        /**
         * Set the API base URL.
         * <p>
         * The specified endpoint values (such as with {@link GET @GET}) are resolved against this
         * value using {@link HttpUrl#resolve(String)}. The behavior of this matches that of an
         * {@code <a href="">} link on a website resolving on the current URL.
         * <p>
         * <b>Base URLs should always end in {@code /}.</b>
         * <p>
         * A trailing {@code /} ensures that endpoints values which are relative paths will correctly
         * append themselves to a base which has path components.
         * <p>
         * <b>Correct:</b><br>
         * Base URL: http://example.com/api/<br>
         * Endpoint: foo/bar/<br>
         * Result: http://example.com/api/foo/bar/
         * <p>
         * <b>Incorrect:</b><br>
         * Base URL: http://example.com/api<br>
         * Endpoint: foo/bar/<br>
         * Result: http://example.com/foo/bar/
         * <p>
         * This method enforces that {@code baseUrl} has a trailing {@code /}.
         * <p>
         * <b>Endpoint values which contain a leading {@code /} are absolute.</b>
         * <p>
         * Absolute values retain only the host from {@code baseUrl} and ignore any specified path
         * components.
         * <p>
         * Base URL: http://example.com/api/<br>
         * Endpoint: /foo/bar/<br>
         * Result: http://example.com/foo/bar/
         * <p>
         * Base URL: http://example.com/<br>
         * Endpoint: /foo/bar/<br>
         * Result: http://example.com/foo/bar/
         * <p>
         * <b>Endpoint values may be a full URL.</b>
         * <p>
         * Values which have a host replace the host of {@code baseUrl} and values also with a scheme
         * replace the scheme of {@code baseUrl}.
         * <p>
         * Base URL: http://example.com/<br>
         * Endpoint: https://github.com/square/retrofit/<br>
         * Result: https://github.com/square/retrofit/
         * <p>
         * Base URL: http://example.com<br>
         * Endpoint: //github.com/square/retrofit/<br>
         * Result: http://github.com/square/retrofit/ (note the scheme stays 'http')
         */
        private Builder restBaseUrl(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "baseUrl == null");
            List<String> pathSegments = baseUrl.pathSegments();
            if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
                throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
            }
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder tokenProvider(TokenProvider provider) {
            this.provider = checkNotNull(provider, "provider == null");
            return this;
        }

        public RocketChatAPI build() {
            return new RocketChatAPI(this);
        }
    }
}
