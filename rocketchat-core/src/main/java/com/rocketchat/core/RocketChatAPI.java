package com.rocketchat.core;

import com.rocketchat.common.data.lightdb.GlobalDbManager;
import com.rocketchat.common.data.model.Room;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleListener;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.core.callback.AccountListener;
import com.rocketchat.core.callback.EmojiListener;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.callback.GetSubscriptionListener;
import com.rocketchat.core.callback.HistoryListener;
import com.rocketchat.core.callback.LoginListener;
import com.rocketchat.core.callback.MessageListener;
import com.rocketchat.core.callback.RoomListener;
import com.rocketchat.core.callback.UserListener;
import com.rocketchat.core.db.RoomDbManager;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.middleware.CoreMiddleware;
import com.rocketchat.core.middleware.CoreStreamMiddleware;
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.SubscriptionObject;
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
import java.io.File;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONException;
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
    private GlobalDbManager globalDbManager;

    // chatRoomFactory class
    private ChatRoomFactory chatRoomFactory;

    public RocketChatAPI(String url) {
        super(url);
        integer = new AtomicInteger(1);
        coreMiddleware = new CoreMiddleware();
        coreStreamMiddleware = new CoreStreamMiddleware();
        globalDbManager = new GlobalDbManager();
        chatRoomFactory = new ChatRoomFactory(this);
    }

    public String getMyUserName() {
        return globalDbManager.getUserCollection().get(userId).getUserName();
    }

    public String getMyUserId() {
        return userId;
    }

    public ChatRoomFactory getChatRoomFactory() {
        return chatRoomFactory;
    }

    public GlobalDbManager getGlobalDbManager() {
        return globalDbManager;
    }

    //Tested
    public void login(String username, String password, LoginListener loginListener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, loginListener, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.login(uniqueID, username, password));
    }

    //Tested
    public void loginUsingToken(String token, LoginListener loginListener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, loginListener, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.loginUsingToken(uniqueID, token));
    }

    //Tested
    public void getPermissions(AccountListener.getPermissionsListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.GET_PERMISSIONS);
        sendDataInBackground(AccountRPC.getPermissions(uniqueID, null));
    }

    //Tested
    public void getPublicSettings(AccountListener.getPublicSettingsListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.GET_PUBLIC_SETTINGS);
        sendDataInBackground(AccountRPC.getPublicSettings(uniqueID, null));
    }

    //Tested
    public void getUserRoles(UserListener.getUserRoleListener userRoleListener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, userRoleListener, CoreMiddleware.ListenerType.GET_USER_ROLES);
        sendDataInBackground(BasicRPC.getUserRoles(uniqueID));
    }

    //Tested
    public void listCustomEmoji(EmojiListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.LIST_CUSTOM_EMOJI);
        sendDataInBackground(BasicRPC.listCustomEmoji(uniqueID));
    }

    //Tested
    public void logout(SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.LOGOUT);
        sendDataInBackground(BasicRPC.logout(uniqueID));
    }

    //Tested
    public void getSubscriptions(GetSubscriptionListener getSubscriptionListener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, getSubscriptionListener, CoreMiddleware.ListenerType.GET_SUBSCRIPTIONS);
        sendDataInBackground(BasicRPC.getSubscriptions(uniqueID));
    }

    //Tested
    public void getRooms(RoomListener.GetRoomListener getRoomListener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, getRoomListener, CoreMiddleware.ListenerType.GET_ROOMS);
        sendDataInBackground(BasicRPC.getRooms(uniqueID));
    }

    //Tested
    private void getRoomRoles(String roomId, RoomListener.RoomRolesListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.GET_ROOM_ROLES);
        sendDataInBackground(BasicRPC.getRoomRoles(uniqueID, roomId));
    }

    //Tested
    private void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.LOAD_HISTORY);
        sendDataInBackground(ChatHistoryRPC.loadHistory(uniqueID, roomID, oldestMessageTimestamp, limit, lasttimestamp));
    }

    private void getRoomMembers(String roomID, Boolean allUsers, RoomListener.GetMembersListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.GET_ROOM_MEMBERS);
        sendDataInBackground(RoomRPC.getRoomMembers(uniqueID, roomID, allUsers));
    }

    //Tested
    private void sendIsTyping(String roomId, String username, Boolean istyping) {
        int uniqueID = integer.getAndIncrement();
        sendDataInBackground(TypingRPC.sendTyping(uniqueID, roomId, username, istyping));
    }

    //Tested
    private void sendMessage(String msgId, String roomID, String message, MessageListener.MessageAckListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.SEND_MESSAGE);
        sendDataInBackground(MessageRPC.sendMessage(uniqueID, msgId, roomID, message));
    }

    //Tested
    private void deleteMessage(String msgId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.deleteMessage(uniqueID, msgId));
    }

    //Tested
    private void updateMessage(String msgId, String roomId, String message, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.updateMessage(uniqueID, msgId, roomId, message));
    }

    //Tested
    private void pinMessage(JSONObject message, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.pinMessage(uniqueID, message));
    }

    //Tested
    private void unpinMessage(JSONObject message, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.unpinMessage(uniqueID, message));
    }

    //Tested
    private void starMessage(String msgId, String roomId, Boolean starred, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.starMessage(uniqueID, msgId, roomId, starred));
    }

    //Tested
    private void setReaction(String emojiId, String msgId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.MESSAGE_OP);
        sendDataInBackground(MessageRPC.setReaction(uniqueID, emojiId, msgId));
    }

    private void searchMessage(String message, String roomId, int limit, MessageListener.SearchMessageListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.SEARCH_MESSAGE);
        sendDataInBackground(MessageRPC.searchMessage(uniqueID, message, roomId, limit));
    }

    //Tested
    public void createPublicGroup(String groupName, String[] users, Boolean readOnly, RoomListener.GroupListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.CREATE_GROUP);
        sendDataInBackground(RoomRPC.createPublicGroup(uniqueID, groupName, users, readOnly));
    }

    //Tested
    public void createPrivateGroup(String groupName, String[] users, RoomListener.GroupListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.CREATE_GROUP);
        sendDataInBackground(RoomRPC.createPrivateGroup(uniqueID, groupName, users));
    }

    //Tested
    private void deleteGroup(String roomId, SimpleListener listener) {
        //Apply simpleListener
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.DELETE_GROUP);
        sendDataInBackground(RoomRPC.deleteGroup(uniqueID, roomId));
    }

    //Tested
    private void archiveRoom(String roomId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.ARCHIVE);
        sendDataInBackground(RoomRPC.archieveRoom(uniqueID, roomId));
    }

    //Tested
    private void unarchiveRoom(String roomId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.UNARCHIVE);
        sendDataInBackground(RoomRPC.unarchiveRoom(uniqueID, roomId));
    }

    //Tested
    public void joinPublicGroup(String roomId, String joinCode, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.JOIN_PUBLIC_GROUP);
        sendDataInBackground(RoomRPC.joinPublicGroup(uniqueID, roomId, joinCode));
    }

    //Tested
    private void leaveGroup(String roomId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.LEAVE_GROUP);
        sendDataInBackground(RoomRPC.leaveGroup(uniqueID, roomId));
    }

    //Tested
    private void hideRoom(String roomId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.HIDE_ROOM);
        sendDataInBackground(RoomRPC.hideRoom(uniqueID, roomId));
    }

    //Tested
    private void openRoom(String roomId, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.OPEN_ROOM);
        sendDataInBackground(RoomRPC.openRoom(uniqueID, roomId));
    }

    //Tested
    private void setFavouriteRoom(String roomId, Boolean isFavouriteRoom, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.SET_FAVOURITE_ROOM);
        sendDataInBackground(RoomRPC.setFavouriteRoom(uniqueID, roomId, isFavouriteRoom));
    }

    private void sendFileMessage(String roomId, String store, String fileId, String fileType, int size, String fileName, String desc, String url, MessageListener.MessageAckListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.SEND_MESSAGE);
        sendDataInBackground(MessageRPC.sendFileMessage(uniqueID, roomId, store, fileId, fileType, size, fileName, desc, url));
    }

    //Tested
    public void setStatus(UserObject.Status s, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.SET_STATUS);
        sendDataInBackground(PresenceRPC.setDefaultStatus(uniqueID, s));
    }

    public void subscribeActiveUsers(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeActiveUsers(uniqueID));
    }

    public void subscribeUserData(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeUserData(uniqueID));
    }

    public void subscribeUserRoles(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeUserRoles(uniqueID));
    }

    public void subscribeLoginConf(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeLoginServiceConfiguration(uniqueID));
    }

    public void subscribeClientVersions(SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeClientVersions(uniqueID));
    }

    private String subscribeRoomFiles(String roomId, int limit, SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeRoomFiles(uniqueID, roomId, limit));
        return uniqueID;
    }

    private String subscribeMentionedMessages(String roomId, int limit, SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeMentionedMessages(uniqueID, roomId, limit));
        return uniqueID;
    }

    private String subscribeStarredMessages(String roomId, int limit, SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeStarredMessages(uniqueID, roomId, limit));
        return uniqueID;
    }

    private String subscribePinnedMessages(String roomId, int limit, SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribePinnedMessages(uniqueID, roomId, limit));
        return uniqueID;
    }

    private String subscribeSnipettedMessages(String roomId, int limit, SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeSnipettedMessages(uniqueID, roomId, limit));
        return uniqueID;
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

    private String subscribeRoomDeleteEvent(String roomId, Boolean enable, SubscribeListener subscribeListener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        sendDataInBackground(CoreSubRPC.subscribeRoomMessageDeleteEvent(uniqueID, roomId, enable));
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
                processCollectionsRemoved(object);
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

        switch (GlobalDbManager.getCollectionType(object)) {
            case OTHER_COLLECTION:
                ChatRoom room = chatRoomFactory.getChatRoomById(getRoomIdFromCollection(object));
                if (room != null) {
//                    System.out.println("Got into room " + room.getRoomData().getRoomName());
                    room.getRoomDbManager().update(object, RPC.MsgType.ADDED);
                } else {
                    System.out.println("Room not found for subscribed room");
                }
                break;
            case GLOBAL_COLLECTION:
                globalDbManager.update(object, RPC.MsgType.ADDED);
                break;
        }

    }

    private void processCollectionsRemoved(JSONObject object) {
        switch (GlobalDbManager.getCollectionType(object)) {
            case OTHER_COLLECTION:
                System.out.println("Local collection " + object.toString());
                ChatRoom room = chatRoomFactory.getChatRoomById(getRoomIdFromCollection(object));
                if (room != null) {
                    System.out.println("Got into room " + room.getRoomData().getRoomName());
                    room.getRoomDbManager().update(object, RPC.MsgType.REMOVED);
                } else {
                    System.out.println("Room not found for subscribed room");
                }
                break;
            case GLOBAL_COLLECTION:
                globalDbManager.update(object, RPC.MsgType.REMOVED);
                break;
        }
    }

    private String getRoomIdFromCollection(JSONObject object) {
        String roomId = null;
        try {
            roomId = object.getJSONObject("fields").getString("rid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roomId;
    }

    private void processCollectionsChanged(JSONObject object) {
        switch (GlobalDbManager.getCollectionType(object)) {
            case OTHER_COLLECTION:
                switch (RoomDbManager.getCollectionType(object)) {
                    case STREAM_COLLECTION:
                        coreStreamMiddleware.processCallback(object);
                        break;
                    case LOCAL_COLLECTION:
                        System.out.println("Local collection " + object.toString());
                        ChatRoom room = chatRoomFactory.getChatRoomById(getRoomIdFromCollection(object));
                        if (room != null) {
                            System.out.println("Got into room " + room.getRoomData().getRoomName());
                            room.getRoomDbManager().update(object, RPC.MsgType.CHANGED);
                        } else {
                            System.out.println("Room not found for subscribed room");
                        }
                        break;
                }
                break;
            case GLOBAL_COLLECTION:
                globalDbManager.update(object, RPC.MsgType.CHANGED);
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
        RoomDbManager roomDbManager;

        //Subscription Ids for new subscriptions
        private String roomSubId;  // TODO: 29/7/17 check for persistent SubscriptionId of the room
        private String typingSubId;
        private String deleteSubId;


        //subscription Ids for room collections
        private String filesSubId;
        private String mentionedMessagesSubId;
        private String starredMessagesSubId;
        private String pinnedMessagesSubId;
        private String snipetedMessagesSubId;


        public ChatRoom(Room room) {
            this.room = room;
            roomDbManager = new RoomDbManager();
        }

        public Boolean isSubscriptionObject() {
            return room instanceof SubscriptionObject;
        }

        public Room getRoomData() {
            return room;
        }

        public RoomDbManager getRoomDbManager() {
            return roomDbManager;
        }

        //RPC methods

        public void getRoomRoles(RoomListener.RoomRolesListener listener) {
            RocketChatAPI.this.getRoomRoles(room.getRoomId(), listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener) {
            RocketChatAPI.this.getChatHistory(room.getRoomId(), limit, oldestMessageTimestamp, lasttimestamp, listener);
        }

        public void getMembers(RoomListener.GetMembersListener membersListener) {
            RocketChatAPI.this.getRoomMembers(room.getRoomId(), false, membersListener);
        }

        public void sendIsTyping(Boolean istyping) {
            RocketChatAPI.this.sendIsTyping(room.getRoomId(), getMyUserName(), istyping);
        }

        public void sendMessage(String message) {
            RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message, null);
        }

        public void sendMessage(String message, MessageListener.MessageAckListener listener) {
            RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message, listener);
        }

        // TODO: 27/7/17 Need more attention on replying to message
        private void replyMessage(RocketChatMessage msg, String message, MessageListener.MessageAckListener listener) {
            message = "[ ](?msg=" + msg.getMessageId() + ") @" + msg.getSender().getUserName() + " " + message;
            RocketChatAPI.this.sendMessage(Utils.shortUUID(), room.getRoomId(), message, listener);
        }

        public void deleteMessage(String msgId, SimpleListener listener) {
            RocketChatAPI.this.deleteMessage(msgId, listener);
        }

        public void updateMessage(String msgId, String message, SimpleListener listener) {
            RocketChatAPI.this.updateMessage(msgId, room.getRoomId(), message, listener);
        }

        public void pinMessage(JSONObject message, SimpleListener listener) {
            RocketChatAPI.this.pinMessage(message, listener);
        }

        public void unpinMessage(JSONObject message, SimpleListener listener) {
            RocketChatAPI.this.unpinMessage(message, listener);
        }

        public void starMessage(String msgId, Boolean starred, SimpleListener listener) {
            RocketChatAPI.this.starMessage(msgId, room.getRoomId(), starred, listener);
        }

        public void setReaction(String emojiId, String msgId, SimpleListener listener) {
            RocketChatAPI.this.setReaction(emojiId, msgId, listener);
        }

        public void searchMessage(String message, int limit, MessageListener.SearchMessageListener listener) {
            RocketChatAPI.this.searchMessage(message, room.getRoomId(), limit, listener);
        }

        public void deleteGroup(SimpleListener listener) {
            RocketChatAPI.this.deleteGroup(room.getRoomId(), listener);
        }

        public void archive(SimpleListener listener) {
            RocketChatAPI.this.archiveRoom(room.getRoomId(), listener);
        }

        public void unarchive(SimpleListener listener) {
            RocketChatAPI.this.unarchiveRoom(room.getRoomId(), listener);
        }

        public void leave(SimpleListener listener) {
            RocketChatAPI.this.leaveGroup(room.getRoomId(), listener);
        }

        public void hide(SimpleListener listener) {
            RocketChatAPI.this.hideRoom(room.getRoomId(), listener);
        }

        public void open(SimpleListener listener) {
            RocketChatAPI.this.openRoom(room.getRoomId(), listener);
        }

        public void uploadFile(File file, String newName, String description, FileListener fileListener) {
            FileUploader uploader = new FileUploader(RocketChatAPI.this, file, newName, description, this, fileListener);
            uploader.startUpload();
        }

        public void sendFileMessage(FileObject file, MessageListener.MessageAckListener listener) {
            RocketChatAPI.this.sendFileMessage(room.getRoomId(), file.getStore(), file.getFileId(), file.getFileType(), file.getSize(), file.getFileName(), file.getDescription(), file.getUrl(), listener);
        }

        public void setFavourite(Boolean isFavoutite, SimpleListener listener) {
            RocketChatAPI.this.setFavouriteRoom(room.getRoomId(), isFavoutite, listener);
        }

        //Subscription methods

        public void subscribeRoomMessageEvent(SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener) {
            if (roomSubId == null) {
                roomSubId = RocketChatAPI.this.subscribeRoomMessageEvent(room.getRoomId(), true, subscribeListener, listener);
                deleteSubId = RocketChatAPI.this.subscribeRoomDeleteEvent(room.getRoomId(), true, null);
            }
        }

        public void subscribeRoomTypingEvent(SubscribeListener subscribeListener, TypingListener listener) {
            if (typingSubId == null) {
                typingSubId = RocketChatAPI.this.subscribeRoomTypingEvent(room.getRoomId(), true, subscribeListener, listener);
            }
        }


        public void unsubscribeRoomMessageEvent(SubscribeListener subscribeListener) {
            if (roomSubId != null) {
                coreStreamMiddleware.removeSub(room.getRoomId(), CoreStreamMiddleware.SubType.SUBSCRIBE_ROOM_MESSAGE);
                RocketChatAPI.this.unsubscribeRoom(roomSubId, subscribeListener);
                RocketChatAPI.this.unsubscribeRoom(deleteSubId, null);
                roomSubId = null;
                deleteSubId = null;
            }
        }

        public void unsubscribeRoomTypingEvent(SubscribeListener subscribeListener) {
            if (typingSubId != null) {
                coreStreamMiddleware.removeSub(room.getRoomId(), CoreStreamMiddleware.SubType.SUBSCRIBE_ROOM_TYPING);
                RocketChatAPI.this.unsubscribeRoom(typingSubId, subscribeListener);
                typingSubId = null;
            }
        }


        // Subscription methods available for flex-tab-container border-component-color on the right side

        public void subscribeRoomFiles(int limit, SubscribeListener listener) {
            if (filesSubId == null) {
                filesSubId = RocketChatAPI.this.subscribeRoomFiles(room.getRoomId(), limit, listener);
            }
        }

        public void subscribeMentionedMessages(int limit, SubscribeListener listener) {
            if (mentionedMessagesSubId == null) {
                mentionedMessagesSubId = RocketChatAPI.this.subscribeMentionedMessages(room.getRoomId(), limit, listener);
            }
        }

        public void subscribeStarredMessages(int limit, SubscribeListener listener) {
            if (starredMessagesSubId == null) {
                starredMessagesSubId = RocketChatAPI.this.subscribeStarredMessages(room.getRoomId(), limit, listener);
            }
        }

        public void subscribePinnedMessages(int limit, SubscribeListener listener) {
            if (pinnedMessagesSubId == null) {
                pinnedMessagesSubId = RocketChatAPI.this.subscribePinnedMessages(room.getRoomId(), limit, listener);
            }
        }

        public void subscribeSnipettedMessages(int limit, SubscribeListener listener) {
            if (snipetedMessagesSubId == null) {
                snipetedMessagesSubId = RocketChatAPI.this.subscribeSnipettedMessages(room.getRoomId(), limit, listener);
            }
        }

        public void unsubscribeRoomFiles(SubscribeListener subscribeListener) {
            if (filesSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(filesSubId, subscribeListener);
                filesSubId = null;
            }
        }

        public void unsubscribeMentionedMessages(SubscribeListener subscribeListener) {
            if (mentionedMessagesSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(mentionedMessagesSubId, subscribeListener);
                mentionedMessagesSubId = null;
            }
        }

        public void unsubscribeStarredMessages(SubscribeListener subscribeListener) {
            if (starredMessagesSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(starredMessagesSubId, subscribeListener);
                starredMessagesSubId = null;
            }
        }

        public void unsubscribePinnedMessages(SubscribeListener subscribeListener) {
            if (pinnedMessagesSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(pinnedMessagesSubId, subscribeListener);
                pinnedMessagesSubId = null;
            }
        }

        public void unsubscribeSnipettedMessages(SubscribeListener subscribeListener) {
            if (snipetedMessagesSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(snipetedMessagesSubId, subscribeListener);
                snipetedMessagesSubId = null;
            }
        }

        public void unsubscribeAllEvents() {
            coreStreamMiddleware.removeAllSub(room.getRoomId());
            unsubscribeRoomMessageEvent(null);
            unsubscribeRoomTypingEvent(null);
            unsubscribeRoomFiles(null);
            unsubscribeMentionedMessages(null);
            unsubscribePinnedMessages(null);
            unsubscribeStarredMessages(null);
            unsubscribeSnipettedMessages(null);
        }

        // TODO: 29/7/17 refresh methods to be added, changing data should change internal data, maintain state of the room
    }


    public void createUFS(String fileName, int fileSize, String fileType, String roomId, String description, String store, IFileUpload.UfsCreateListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.UFS_CREATE);
        sendDataInBackground(FileUploadRPC.ufsCreate(uniqueID, fileName, fileSize, fileType, roomId, description, store));
    }

    public void completeUFS(String fileId, String store, String token, IFileUpload.UfsCompleteListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.UFS_COMPLETE);
        sendDataInBackground(FileUploadRPC.ufsComplete(uniqueID, fileId, store, token));
    }
}
