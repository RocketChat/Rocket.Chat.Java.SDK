package com.rocketchat.core;

import com.rocketchat.common.data.lightdb.DbManager;
import com.rocketchat.common.data.model.Room;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.core.callback.MessageListener;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.middleware.CoreMiddleware;
import com.rocketchat.core.middleware.CoreStreamMiddleware;
import com.rocketchat.core.model.*;
import com.rocketchat.core.model.result.GetRoomMembersResult;
import com.rocketchat.core.model.result.LoadHistoryResult;
import com.rocketchat.core.rpc.AccountRPC;
import com.rocketchat.core.rpc.BasicRPC;
import com.rocketchat.core.rpc.ChatHistoryRPC;
import com.rocketchat.core.rpc.CoreSubRPC;
import com.rocketchat.core.rpc.FileUploadRPC;
import com.rocketchat.core.rpc.MessageRPC;
import com.rocketchat.core.rpc.RoomRPC;
import com.rocketchat.core.rpc.TypingRPC;
import com.rocketchat.core.uploader.FileUploadToken;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 30/7/17 Make it singletone like eventbus, add builder class to RocketChatAPI in order to use it anywhere, maybe a common builder class
public class RocketChatAPI extends Socket {

    private AtomicInteger integer;
    private String sessionId;
    private String userId;

    private CoreMiddleware coreMiddleware;
    private CoreStreamMiddleware coreStreamMiddleware;
    private DbManager dbManager;

    // chatRoomFactory class
    private ChatRoomFactory chatRoomFactory;
    private CompletableFuture<Void> connectResult;

    public RocketChatAPI(String url) {
        super(url);
        integer = new AtomicInteger(1);
        coreMiddleware = new CoreMiddleware();
        coreStreamMiddleware = new CoreStreamMiddleware();
        dbManager = new DbManager();
        chatRoomFactory = new ChatRoomFactory(this);
    }

    public String getMyUserName() {
        return dbManager.getUserCollection().get(userId).getUserName();
    }

    public ChatRoomFactory getChatRoomFactory() {
        return chatRoomFactory;
    }

    //Tested
    public CompletableFuture<TokenObject> login(String username, String password) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<TokenObject> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.login(uniqueID, username, password));
        return result;
    }

    //Tested
    public CompletableFuture<TokenObject> loginUsingToken(String token) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<TokenObject> result = coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.loginUsingToken(uniqueID, token));
        return result;
    }

    //Tested
    public CompletableFuture<List<Permission>> getPermissions() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<Permission>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_PERMISSIONS);
        sendDataInBackground(AccountRPC.getPermissions(uniqueID, null));
        return result;
    }

    //Tested
    public CompletableFuture<List<PublicSetting>> getPublicSettings() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<PublicSetting>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_PUBLIC_SETTINGS);
        sendDataInBackground(AccountRPC.getPublicSettings(uniqueID, null));
        return result;
    }

    //Tested
    public CompletableFuture<List<UserObject>> getUserRoles() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<UserObject>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_USER_ROLES);
        sendDataInBackground(BasicRPC.getUserRoles(uniqueID));
        return result;
    }

    //Tested
    public CompletableFuture<List<Emoji>> listCustomEmoji() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<Emoji>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.LIST_CUSTOM_EMOJI);
        sendDataInBackground(BasicRPC.listCustomEmoji(uniqueID));
        return result;
    }

    //Tested
    public CompletableFuture<Boolean> logout() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.LOGOUT);
        sendDataInBackground(BasicRPC.logout(uniqueID));
        return result;
    }

    //Tested
    public CompletableFuture<List<SubscriptionObject>> getSubscriptions() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<SubscriptionObject>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_SUBSCRIPTIONS);
        sendDataInBackground(BasicRPC.getSubscriptions(uniqueID));
        return result;
    }

    //Tested
    public CompletableFuture<List<RoomObject>> getRooms() {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<RoomObject>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_ROOMS);
        sendDataInBackground(BasicRPC.getRooms(uniqueID));
        return result;
    }

    //Tested
    private CompletableFuture<List<RoomRole>> getRoomRoles(String roomId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<RoomRole>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_ROOM_ROLES);
        sendDataInBackground(BasicRPC.getRoomRoles(uniqueID, roomId));
        return result;
    }

    //Tested
    private CompletableFuture<LoadHistoryResult> getChatHistory(String roomID, int limit, Date oldestMessageTimestamp, Date lasttimestamp) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<LoadHistoryResult> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.LOAD_HISTORY);
        sendDataInBackground(ChatHistoryRPC.loadHistory(uniqueID, roomID, oldestMessageTimestamp, limit, lasttimestamp));
        return result;
    }

    private CompletableFuture<GetRoomMembersResult> getRoomMembers(String roomID, Boolean allUsers) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<GetRoomMembersResult> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.GET_ROOM_MEMBERS);
        sendDataInBackground(RoomRPC.getRoomMembers(uniqueID, roomID, allUsers));
        return result;
    }

    //Tested
    private void sendIsTyping(String roomId, String username, Boolean istyping) {
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(TypingRPC.sendTyping(uniqueID, roomId, username, istyping));
    }

    //Tested
    private CompletableFuture<RocketChatMessage> sendMessage(String msgId, String roomID, String message) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<RocketChatMessage> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.SEND_MESSAGE);
        sendDataInBackground(MessageRPC.sendMessage(uniqueID, msgId, roomID, message));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> deleteMessage(String msgId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.deleteMessage(uniqueID, msgId));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> updateMessage(String msgId, String roomId, String message) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.updateMessage(uniqueID, msgId, roomId, message));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> pinMessage(JSONObject message) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.pinMessage(uniqueID, message));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> unpinMessage(JSONObject message) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.unpinMessage(uniqueID, message));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> starMessage(String msgId, String roomId, Boolean starred) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.starMessage(uniqueID, msgId, roomId, starred));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> setReaction(String emojiId, String msgId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.setReaction(uniqueID, emojiId, msgId));
        return result;
    }

    private CompletableFuture<List<RocketChatMessage>> searchMessage(String message, String roomId, int limit) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<List<RocketChatMessage>> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.SEARCH_MESSAGE);
        sendDataInBackground(MessageRPC.searchMessage(uniqueID, message, roomId, limit));
        return result;
    }

    /**
     * @return roomId
     */
    //Tested
    public CompletableFuture<String> createPublicGroup(String groupName, String[] users, Boolean readOnly) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<String> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.CREATE_GROUP);
        sendDataInBackground(RoomRPC.createPublicGroup(uniqueID, groupName, users, readOnly));
        return result;
    }

    /**
     * @return roomId
     */
    //Tested
    public CompletableFuture<String> createPrivateGroup(String groupName, String[] users) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<String> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.CREATE_GROUP);
        sendDataInBackground(RoomRPC.createPrivateGroup(uniqueID, groupName, users));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> deleteGroup(String roomId) {
        //Apply simpleListener
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.DELETE_GROUP);
        sendDataInBackground(RoomRPC.deleteGroup(uniqueID, roomId));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> archiveRoom(String roomId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.ARCHIVE);
        sendDataInBackground(RoomRPC.archieveRoom(uniqueID, roomId));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> unarchiveRoom(String roomId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.UNARCHIVE);
        sendDataInBackground(RoomRPC.unarchiveRoom(uniqueID, roomId));
        return result;
    }

    //Tested
    public CompletableFuture<Boolean> joinPublicGroup(String roomId, String joinCode) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.JOIN_PUBLIC_GROUP);
        sendDataInBackground(RoomRPC.joinPublicGroup(uniqueID, roomId, joinCode));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> leaveGroup(String roomId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.LEAVE_GROUP);
        sendDataInBackground(RoomRPC.leaveGroup(uniqueID, roomId));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> hideRoom(String roomId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.HIDE_ROOM);
        sendDataInBackground(RoomRPC.hideRoom(uniqueID, roomId));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> openRoom(String roomId) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.OPEN_ROOM);
        sendDataInBackground(RoomRPC.openRoom(uniqueID, roomId));
        return result;
    }

    //Tested
    private CompletableFuture<Boolean> setFavouriteRoom(String roomId, Boolean isFavouriteRoom) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<Boolean> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.SET_FAVOURITE_ROOM);
        sendDataInBackground(RoomRPC.setFavouriteRoom(uniqueID, roomId, isFavouriteRoom));
        return result;
    }

    private CompletableFuture<RocketChatMessage> sendFileMessage(String roomId, String store, String fileId, String fileType, int size, String fileName, String desc, String url) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<RocketChatMessage> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.SEND_MESSAGE);
        sendDataInBackground(MessageRPC.sendFileMessage(uniqueID, roomId, store, fileId, fileType, size, fileName, desc, url));
        return result;
    }

    //Tested
    private String subscribeRoomMessageEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        coreStreamMiddleware.createSub(roomId, listener, CoreStreamMiddleware.SubType.SUBSCRIBE_ROOM_MESSAGE);
        sendDataInBackground(CoreSubRPC.subscribeRoomMessageEvent(uniqueID, roomId, enable));
        return uniqueID;
    }

    private String subscribeRoomTypingEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, TypingListener listener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        coreStreamMiddleware.createSub(roomId, listener, CoreStreamMiddleware.SubType.SUBSCRIBE_ROOM_TYPING);
        sendDataInBackground(CoreSubRPC.subscribeRoomTypingEvent(uniqueID, roomId, enable));
        return uniqueID;
    }

    private void unsubscribeRoom(String subId, SubscribeListener subscribeListener) {
        sendDataInBackground(CoreSubRPC.unsubscribeRoom(subId));
        coreStreamMiddleware.createSubCallback(subId, subscribeListener);
    }

    public void connect(ConnectListener connectListener) {
        createSocket();
        connectivityManager.register(connectListener);
        super.connectAsync();
    }

    public CompletableFuture<Void> singleConnect() {
        if (connectResult == null) {
            connectResult = new CompletableFuture<>();
            connect(new ConnectListener() {
                @Override
                public void onConnect(String sessionID) {
                    connectResult.complete(null);
                }

                @Override
                public void onDisconnect(boolean closedByServer) {
                }

                @Override
                public void onConnectError(Exception websocketException) {
                    connectResult.completeExceptionally(websocketException);
                }
            });
        }
        return connectResult;
    }


    @Override
    protected void onConnected() {
        integer.set(1);
        sendDataInBackground(BasicRPC.ConnectObject());
        super.onConnected();
    }

    @Override
    protected void onTextMessage(String text) throws Exception {
        super.onTextMessage(text);
        JSONObject object = new JSONObject(text);
        switch (RPC.parse(object.optString("msg"))) {
            case PING:
                sendDataInBackground(BasicRPC.PONG_MESSAGE);
                break;
            case PONG:
                sendPingFrames();
                break;
            case CONNECTED:
                processOnConnected(object);
                break;
            case RESULT:
                coreMiddleware.processCallback(Long.valueOf(object.optString("id")), object);
                break;
            case READY:
                coreStreamMiddleware.processSubSuccess(object);
                break;
            case ADDED:
                processCollectionsAdded(object);
                break;
            case CHANGED:
                processCollectionsChanged(object);
                break;
            case REMOVED:
                dbManager.update(object, RPC.MsgType.REMOVED);
                break;
            case NOSUB:
                coreStreamMiddleware.processUnsubSuccess(object);
                break;
            case OTHER:
                break;
            default:

                break;
        }
    }

    private void sendPingFrames() {
        if (isPingEnabled()) {
            sendPingFramesPeriodically();
        }
    }

    private void processOnConnected(JSONObject object) {
        sessionId = object.optString("session");
        connectivityManager.publishConnect(sessionId);
        sendDataInBackground(BasicRPC.PING_MESSAGE);
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
                coreStreamMiddleware.processCallback(object);
                break;
            case COLLECTION:
                dbManager.update(object, RPC.MsgType.CHANGED);
                break;
        }
    }


    @Override
    protected void onConnectError(Exception websocketException) {
        connectivityManager.publishConnectError(websocketException);
        super.onConnectError(websocketException);
    }

    @Override
    protected void onDisconnected(boolean closedByServer) {
        connectivityManager.publishDisconnect(closedByServer);
        super.onDisconnected(closedByServer);
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

        public Room getRoomData() {
            return room;
        }

        //RPC methods

        public CompletableFuture<LoadHistoryResult> getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp) {
            return RocketChatAPI.this.getChatHistory(room.getRoomId(), limit, oldestMessageTimestamp, lasttimestamp);
        }

        public CompletableFuture<RocketChatMessage> sendMessage(String message) {
            return RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message);
        }

        public CompletableFuture<Boolean> deleteMessage(String msgId) {
            return RocketChatAPI.this.deleteMessage(msgId);
        }

        public CompletableFuture<Boolean> updateMessage(String msgId, String message) {
            return RocketChatAPI.this.updateMessage(msgId, room.getRoomId(), message);
        }

        public CompletableFuture<Boolean> pinMessage(JSONObject message) {
            return RocketChatAPI.this.pinMessage(message);
        }

        public CompletableFuture<Boolean> unpinMessage(JSONObject message) {
            return RocketChatAPI.this.unpinMessage(message);
        }

        public CompletableFuture<Boolean> starMessage(String msgId, Boolean starred) {
            return RocketChatAPI.this.starMessage(msgId, room.getRoomId(), starred);
        }

        public CompletableFuture<Boolean> archive() {
            return RocketChatAPI.this.archiveRoom(room.getRoomId());
        }

        public CompletableFuture<Boolean> unarchive() {
            return RocketChatAPI.this.unarchiveRoom(room.getRoomId());
        }

        public CompletableFuture<Boolean> leave() {
            return RocketChatAPI.this.leaveGroup(room.getRoomId());
        }

        public CompletableFuture<Boolean> hide() {
            return RocketChatAPI.this.hideRoom(room.getRoomId());
        }

        public CompletableFuture<Boolean> open() {
            return RocketChatAPI.this.openRoom(room.getRoomId());
        }

        public CompletableFuture<RocketChatMessage> sendFileMessage(FileObject file) {
            return RocketChatAPI.this.sendFileMessage(room.getRoomId(), file.getStore(), file.getFileId(), file.getFileType(), file.getSize(), file.getFileName(), file.getDescription(), file.getUrl());
        }

        public CompletableFuture<Boolean> setFavourite(Boolean isFavoutite) {
            return RocketChatAPI.this.setFavouriteRoom(room.getRoomId(), isFavoutite);
        }

        //Subscription methods

        public void subscribeRoomMessageEvent(SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener) {
            if (roomSubId == null) {
                roomSubId = RocketChatAPI.this.subscribeRoomMessageEvent(room.getRoomId(), true, subscribeListener, listener);
            }
        }

        public void subscribeRoomTypingEvent(SubscribeListener subscribeListener, TypingListener listener) {
            if (typingSubId == null) {
                typingSubId = RocketChatAPI.this.subscribeRoomTypingEvent(room.getRoomId(), true, subscribeListener, listener);
            }
        }

        // TODO: 29/7/17 refresh methods to be added, changing data should change internal data, maintain state of the room
    }


    public CompletableFuture<FileUploadToken> createUFS(String fileName, int fileSize, String fileType, String roomId, String description, String store) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<FileUploadToken> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.UFS_CREATE);
        sendDataInBackground(FileUploadRPC.ufsCreate(uniqueID, fileName, fileSize, fileType, roomId, description, store));
        return result;
    }

    public CompletableFuture<FileObject> completeUFS(String fileId, String store, String token) {
        int uniqueID = integer.getAndIncrement();
        CompletableFuture<FileObject> result =  coreMiddleware.createCallback(uniqueID, CoreMiddleware.ListenerType.UFS_COMPLETE);
        sendDataInBackground(FileUploadRPC.ufsComplete(uniqueID, fileId, store, token));
        return result;
    }
}
