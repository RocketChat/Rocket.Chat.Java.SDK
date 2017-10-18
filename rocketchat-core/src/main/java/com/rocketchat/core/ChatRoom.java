package com.rocketchat.core;

import com.rocketchat.common.data.model.BaseRoom;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.listener.StreamCollectionListener;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.callback.RoomCallback;
import com.rocketchat.core.db.Document.FileDocument;
import com.rocketchat.core.db.Document.MessageDocument;
import com.rocketchat.core.db.LocalStreamCollectionManager;
import com.rocketchat.core.internal.middleware.CoreStreamMiddleware;
import com.rocketchat.core.model.FileDescriptor;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.uploader.FileUploader;

import org.json.JSONObject;

import java.util.Date;

// TODO: 29/7/17 add throw custom exceptions if method call violates permission required to execute given RPC
public class ChatRoom {

    private final RocketChatClient client;
    private final BaseRoom room;
    private LocalStreamCollectionManager localStreamCollectionManager;

    //Subscription Ids for new subscriptions
    private String roomSubId;
    private String typingSubId;
    private String deleteSubId;


    //subscription Ids for room collections
    private String filesSubId;
    private String mentionedMessagesSubId;
    private String starredMessagesSubId;
    private String pinnedMessagesSubId;
    private String snipetedMessagesSubId;

    public ChatRoom(RocketChatClient api, BaseRoom room) {
        this.client = api;
        this.room = room;
        this.localStreamCollectionManager = new LocalStreamCollectionManager();
    }

    public LocalStreamCollectionManager getLocalStreamCollectionManager() {
        return localStreamCollectionManager;
    }

    public Boolean isSubscriptionObject() {
        return room instanceof Subscription;
    }

    public BaseRoom getRoomData() {
        return room;
    }

    //RPC methods

    public void getRoomRoles(SimpleListCallback<RoomRole> callback) {
        client.getRoomRoles(room.roomId(), callback);
    }

    public void getChatHistory(int limit, Date oldestMessageTimestamp, Date lasttimestamp,
                               HistoryCallback callback) {
        client.getChatHistory(room.roomId(), limit, oldestMessageTimestamp, lasttimestamp, callback);
    }

    public void getMembers(RoomCallback.GetMembersCallback callback) {
        client.getRoomMembers(room.roomId(), false, callback);
    }

    public void sendIsTyping(Boolean istyping) {
        client.sendIsTyping(room.roomId(), client.getMyUserName(), istyping);
    }

    public void sendMessage(String message) {
        client.sendMessage(Utils.shortUUID(), room.roomId(), message, null);
    }

    public void sendMessage(String message, MessageCallback.MessageAckCallback callback) {
        client.sendMessage(Utils.shortUUID(), room.roomId(), message, callback);
    }

    // TODO: 27/7/17 Need more attention on replying to message
    private void replyMessage(Message msg, String message,
                              MessageCallback.MessageAckCallback callback) {
            /*message = "[ ](?msg=" + msg.id() + ") @" + msg.sender().getUserName() + " " + message;
            client.sendMessage(Utils.shortUUID(), room.roomId(), message, callback);*/
    }

    public void deleteMessage(String msgId, SimpleCallback callback) {
        client.deleteMessage(msgId, callback);
    }

    public void updateMessage(String msgId, String message, SimpleCallback callback) {
        client.updateMessage(msgId, room.roomId(), message, callback);
    }

    public void pinMessage(String messageId, SimpleCallback callback) {
        client.pinMessage(messageId, callback);
    }

    @Deprecated
    public void pinMessage(JSONObject message, SimpleCallback callback) {
        client.pinMessage(message, callback);
    }

    public void unpinMessage(JSONObject message, SimpleCallback callback) {
        client.unpinMessage(message, callback);
    }

    public void starMessage(String msgId, Boolean starred, SimpleCallback callback) {
        client.starMessage(msgId, room.roomId(), starred, callback);
    }

    public void setReaction(String emojiId, String msgId, SimpleCallback callback) {
        client.setReaction(emojiId, msgId, callback);
    }

    public void searchMessage(String message, int limit,
                              SimpleListCallback<Message> callback) {
        client.searchMessage(message, room.roomId(), limit, callback);
    }

    public void deleteGroup(SimpleCallback callback) {
        client.deleteGroup(room.roomId(), callback);
    }

    public void archive(SimpleCallback callback) {
        client.archiveRoom(room.roomId(), callback);
    }

    public void unarchive(SimpleCallback callback) {
        client.unarchiveRoom(room.roomId(), callback);
    }

    public void leave(SimpleCallback callback) {
        client.leaveGroup(room.roomId(), callback);
    }

    public void hide(SimpleCallback callback) {
        client.hideRoom(room.roomId(), callback);
    }

    public void open(SimpleCallback callback) {
        client.openRoom(room.roomId(), callback);
    }

    public void uploadFile(java.io.File file, String newName, String description, FileListener fileListener) {
        FileUploader uploader = new FileUploader(client, file, newName, description,
                this, fileListener);
        uploader.startUpload();
    }

    public void sendFileMessage(FileDescriptor file, MessageCallback.MessageAckCallback callback) {
        client.sendFileMessage(room.roomId(), file.getStore(), file.getFileId(),
                file.getFileType(), file.getSize(), file.getFileName(), file.getDescription(),
                file.getUrl(), callback);
    }

    public void setFavourite(Boolean isFavoutite, SimpleCallback callback) {
        client.setFavouriteRoom(room.roomId(), isFavoutite, callback);
    }

    //Subscription methods

    public void subscribeRoomMessageEvent(SubscribeListener subscribeListener,
                                          MessageCallback.SubscriptionListener callback) {
        if (roomSubId == null) {
            roomSubId = client.subscribeRoomMessageEvent(room.roomId(),
                    true, subscribeListener, callback);
            deleteSubId = client.subscribeRoomDeleteEvent(room.roomId(), true, null);
        }
    }

    public void subscribeRoomTypingEvent(SubscribeListener subscribeListener, TypingListener listener) {
        if (typingSubId == null) {
            typingSubId = client.subscribeRoomTypingEvent(room.roomId(), true, subscribeListener, listener);
        }
    }

    public void unSubscribeRoomMessageEvent(SubscribeListener subscribeListener) {
        if (roomSubId != null) {
            client.removeSubscription(room.roomId(), CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_MESSAGE);
            client.unsubscribeRoom(roomSubId, subscribeListener);
            client.unsubscribeRoom(deleteSubId, null);
            roomSubId = null;
            deleteSubId = null;
        }
    }

    public void unSubscribeRoomTypingEvent(SubscribeListener subscribeListener) {
        if (typingSubId != null) {
            client.removeSubscription(room.roomId(), CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_TYPING);
            client.unsubscribeRoom(typingSubId, subscribeListener);
            typingSubId = null;
        }
    }

    // Subscription methods available for flex-tab-container border-component-color on the right side

    public void subscribeRoomFiles(int limit, SubscribeListener listener, StreamCollectionListener<FileDocument> roomFilesCollectionListener) {
        if (filesSubId == null) {
            filesSubId = client.subscribeRoomFiles(room.roomId(), limit, listener);
            localStreamCollectionManager.subscribeRoomFilesCollection(roomFilesCollectionListener);
        }
    }

    public void subscribeMentionedMessages(int limit, SubscribeListener listener, StreamCollectionListener<MessageDocument> mentionedMessagesCollectionListener) {
        if (mentionedMessagesSubId == null) {
            mentionedMessagesSubId = client.subscribeMentionedMessages(room.roomId(), limit, listener);
            localStreamCollectionManager.subscribeMentionedMessagesCollection(mentionedMessagesCollectionListener);

        }
    }

    public void subscribeStarredMessages(int limit, SubscribeListener listener, StreamCollectionListener<MessageDocument> starredMessagesCollectionListener) {
        if (starredMessagesSubId == null) {
            starredMessagesSubId = client.subscribeStarredMessages(room.roomId(), limit, listener);
            localStreamCollectionManager.subscribeStarredMessagesCollection(starredMessagesCollectionListener);
        }
    }

    public void subscribePinnedMessages(int limit, SubscribeListener listener,StreamCollectionListener<MessageDocument> pinnedMessagesCollectionListener ) {
        if (pinnedMessagesSubId == null) {
            pinnedMessagesSubId = client.subscribePinnedMessages(room.roomId(), limit, listener);
            localStreamCollectionManager.subscribePinnedMessagesCollection(pinnedMessagesCollectionListener);
        }
    }

    public void subscribeSnipettedMessages(int limit, SubscribeListener listener, StreamCollectionListener<MessageDocument> snipetedMessagesCollectionListener) {
        if (snipetedMessagesSubId == null) {
            snipetedMessagesSubId = client.subscribeSnipettedMessages(room.roomId(), limit, listener);
            localStreamCollectionManager.subscribeSnipetedMessagesCollection(snipetedMessagesCollectionListener);
        }
    }

    public void unsubscribeRoomFiles(SubscribeListener subscribeListener) {
        if (filesSubId != null) {
            client.unsubscribeRoom(filesSubId, subscribeListener);
            filesSubId = null;
        }
    }

    public void unsubscribeMentionedMessages(SubscribeListener subscribeListener) {
        if (mentionedMessagesSubId != null) {
            client.unsubscribeRoom(mentionedMessagesSubId, subscribeListener);
            mentionedMessagesSubId = null;
        }
    }

    public void unsubscribeStarredMessages(SubscribeListener subscribeListener) {
        if (starredMessagesSubId != null) {
            client.unsubscribeRoom(starredMessagesSubId, subscribeListener);
            starredMessagesSubId = null;
        }
    }

    public void unsubscribePinnedMessages(SubscribeListener subscribeListener) {
        if (pinnedMessagesSubId != null) {
            client.unsubscribeRoom(pinnedMessagesSubId, subscribeListener);
            pinnedMessagesSubId = null;
        }
    }

    public void unsubscribeSnipettedMessages(SubscribeListener subscribeListener) {
        if (snipetedMessagesSubId != null) {
            client.unsubscribeRoom(snipetedMessagesSubId, subscribeListener);
            snipetedMessagesSubId = null;
        }
    }

    public void unSubscribeAllEvents() {
        client.removeAllSubscriptions(room.roomId());
        unSubscribeRoomMessageEvent(null);
        unSubscribeRoomTypingEvent(null);
        unsubscribeRoomFiles(null);
        unsubscribeMentionedMessages(null);
        unsubscribePinnedMessages(null);
        unsubscribeStarredMessages(null);
        unsubscribeSnipettedMessages(null);
    }

    // TODO: 29/7/17 refresh methods to be added, changing data should change internal data, maintain state of the room
}
