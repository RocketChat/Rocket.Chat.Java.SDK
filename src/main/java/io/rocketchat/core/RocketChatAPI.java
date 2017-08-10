package io.rocketchat.core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import io.rocketchat.common.data.model.Room;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.listener.ConnectListener;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.common.listener.TypingListener;
import io.rocketchat.common.network.Socket;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.core.callback.AccountListener;
import io.rocketchat.core.callback.EmojiListener;
import io.rocketchat.core.callback.HistoryListener;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.callback.MessageListener;
import io.rocketchat.core.callback.RoomListener;
import io.rocketchat.core.callback.SubscriptionListener;
import io.rocketchat.core.callback.UserListener;
import io.rocketchat.core.factory.ChatRoomFactory;
import io.rocketchat.core.middleware.CoreMiddleware;
import io.rocketchat.core.middleware.CoreStreamMiddleware;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.rpc.AccountRPC;
import io.rocketchat.core.rpc.BasicRPC;
import io.rocketchat.core.rpc.ChatHistoryRPC;
import io.rocketchat.core.rpc.CoreSubRPC;
import io.rocketchat.core.rpc.MessageRPC;
import io.rocketchat.core.rpc.PresenceRPC;
import io.rocketchat.core.rpc.RoomRPC;
import io.rocketchat.core.rpc.TypingRPC;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 30/7/17 Make it singletone like eventbus, add builder class to RocketChatAPI in order to use it anywhere, maybe a common builder class
public class RocketChatAPI extends Socket {

    private AtomicInteger integer;
    private String sessionId;
    private UserObject userInfo;

    private ConnectListener connectListener;

    private CoreMiddleware coreMiddleware;
    private CoreStreamMiddleware coreStreamMiddleware;

    // factory class
    private ChatRoomFactory factory;

    public RocketChatAPI(String url) {
        super(url);
        integer = new AtomicInteger(1);
        coreMiddleware = new CoreMiddleware();
        coreStreamMiddleware = new CoreStreamMiddleware();
        factory = new ChatRoomFactory(this);
    }

    public String getMyUserName() {
        return userInfo.getUserName();
    }

    public JSONArray getMyEmails() {
        return userInfo.getEmails();
    }

    public ChatRoomFactory getFactory() {
        return factory;
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
    public void getSubscriptions(SubscriptionListener.GetSubscriptionListener getSubscriptionListener) {
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

    //Tested
    public void setStatus(PresenceRPC.Status s, SimpleListener listener) {
        int uniqueID = integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID, listener, CoreMiddleware.ListenerType.SET_STATUS);
        sendDataInBackground(PresenceRPC.setDefaultStatus(uniqueID, s));
    }

    //Tested
    private String subscribeRoomMessageEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, MessageListener.SubscriptionListener listener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        coreStreamMiddleware.subscribeRoomMessage(listener);
        sendDataInBackground(CoreSubRPC.subscribeRoomMessageEvent(uniqueID, roomId, enable));
        return uniqueID;
    }

    private String subscribeRoomTypingEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, TypingListener listener) {
        String uniqueID = Utils.shortUUID();
        coreStreamMiddleware.createSubCallback(uniqueID, subscribeListener);
        coreStreamMiddleware.subscribeRoomTyping(listener);
        sendDataInBackground(CoreSubRPC.subscribeRoomTypingEvent(uniqueID, roomId, enable));
        return uniqueID;
    }

    private void unsubscribeRoom(String subId, SubscribeListener subscribeListener) {
        sendDataInBackground(CoreSubRPC.unsubscribeRoom(subId));
        coreStreamMiddleware.createSubCallback(subId, subscribeListener);
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void connect(ConnectListener connectListener) {
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
                sendDataInBackground(BasicRPC.PONGMESSAGE);
                break;
            case PONG:
                if (isPingEnabled()) {
                    sendPingFramesPeriodically();
                }
                break;
            case CONNECTED:
                sessionId = object.optString("session");
                if (connectListener != null) {
                    connectListener.onConnect(sessionId);
                }
                sendDataInBackground(BasicRPC.PINGMESSAGE);
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
            case NOSUB:
                coreStreamMiddleware.processUnsubSuccess(object);
                break;
            case OTHER:
                break;
            default:

                break;
        }

        super.onTextMessage(text);
    }

    @Override
    protected void onConnectError(Exception websocketException) {
        if (connectListener != null) {
            connectListener.onConnectError(websocketException);
        }
        super.onConnectError(websocketException);
    }

    @Override
    protected void onDisconnected(boolean closedByServer) {
        if (connectListener != null) {
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

        public void getRoomRoles(RoomListener.RoomRolesListener listener) {
            RocketChatAPI.this.getRoomRoles(room.getRoomId(), listener);
        }

        public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp, HistoryListener listener) {
            RocketChatAPI.this.getChatHistory(room.getRoomId(), limit, oldestMessageTimestamp, lasttimestamp, listener);
        }

        public void getMembers(Boolean allUsers, RoomListener.GetMembersListener membersListener){
            RocketChatAPI.this.getRoomMembers(room.getRoomId(), allUsers, membersListener);
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

        public void setFavourite(Boolean isFavoutite, SimpleListener listener) {
            RocketChatAPI.this.setFavouriteRoom(room.getRoomId(), isFavoutite, listener);
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

        public void unSubscribeRoomMessageEvent(SubscribeListener subscribeListener) {
            if (roomSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(roomSubId, subscribeListener);
                roomSubId = null;
            }
        }

        public void unSubscribeRoomTypingEvent(SubscribeListener subscribeListener) {
            if (typingSubId != null) {
                RocketChatAPI.this.unsubscribeRoom(typingSubId, subscribeListener);
                typingSubId = null;
            }
        }

        public void unSubscribeAllEvents() {
            unSubscribeRoomMessageEvent(null);
            unSubscribeRoomTypingEvent(null);
        }

        // TODO: 29/7/17 refresh methods to be added, changing data should change internal data, maintain state of the room
    }
}
