package com.rocketchat.core.roomstream;

import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.listener.StreamCollectionListener;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.roomstream.Document.FileDocument;
import com.rocketchat.core.roomstream.Document.MessageDocument;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 16/9/17.
 */

public class LocalStreamCollectionManager {

    private final Moshi moshi;

    StreamCollectionListener<FileDocument> roomFilesCollection;
    StreamCollectionListener<MessageDocument> mentionedMessagesCollection;
    StreamCollectionListener<MessageDocument> starredMessagesCollection;
    StreamCollectionListener<MessageDocument> pinnedMessagesCollection;
    StreamCollectionListener<MessageDocument> snipetedMessagesCollection;

    private static final String COLLECTION_TYPE_FILES = "room_files";
    private static final String COLLECTION_TYPE_MENTIONED_MESSAGES = "rocketchat_mentioned_message";
    private static final String COLLECTION_TYPE_STARRED_MESSAGES = "rocketchat_starred_message";
    private static final String COLLECTION_TYPE_PINNED_MESSAGES = "rocketchat_pinned_message";
    private static final String COLLECTION_TYPE_SNIPETED_MESSAGES = "rocketchat_snippeted_message";

    public LocalStreamCollectionManager(Moshi moshi) {
        this.moshi = moshi;
    }

    public void subscribeRoomFilesCollection(StreamCollectionListener<FileDocument> roomFilesCollection) {
        this.roomFilesCollection = roomFilesCollection;
    }

    public void subscribeMentionedMessagesCollection(StreamCollectionListener<MessageDocument> mentionedMessagesCollection) {
        this.mentionedMessagesCollection = mentionedMessagesCollection;
    }

    public void subscribeStarredMessagesCollection(StreamCollectionListener<MessageDocument> starredMessagesCollection) {
        this.starredMessagesCollection = starredMessagesCollection;
    }

    public void subscribePinnedMessagesCollection(StreamCollectionListener<MessageDocument> pinnedMessagesCollection) {
        this.pinnedMessagesCollection = pinnedMessagesCollection;
    }

    public void subscribeSnipetedMessagesCollection(StreamCollectionListener<MessageDocument> snipetedMessagesCollection) {
        this.snipetedMessagesCollection = snipetedMessagesCollection;
    }

    public void update(JSONObject object, RPC.MsgType type) {
        String collectionName = object.optString("collection");
        switch (collectionName) {
            case COLLECTION_TYPE_FILES:
                updateRoomFiles(object, type);
                break;
            case COLLECTION_TYPE_MENTIONED_MESSAGES:
                updateMentionedMessages(object, type);
                break;
            case COLLECTION_TYPE_PINNED_MESSAGES:
                updatePinnedMessages(object, type);
                break;
            case COLLECTION_TYPE_STARRED_MESSAGES:
                updateStarredMessages(object, type);
                break;
            case COLLECTION_TYPE_SNIPETED_MESSAGES:
                updateSnipettedMessages(object, type);
                break;
        }
    }

    private void updateRoomFiles(JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        if (roomFilesCollection != null) {
            switch (type) {
                case ADDED:
                    FileDocument document = new FileDocument(object.optJSONObject("fields"));
                    document.setId(id);
                    roomFilesCollection.onAdded(id, document);
                    break;
                case CHANGED:
                    FileDocument changedDocument = new FileDocument(object.optJSONObject("fields"));
                    changedDocument.setId(id);
                    roomFilesCollection.onChanged(id, changedDocument);
                    break;
                case REMOVED:
                    roomFilesCollection.onRemoved(id);
                    break;
            }
        }

        System.out.println("Got into update room files");
    }

    private void updateMentionedMessages(JSONObject object, RPC.MsgType type) {
        updateMessageCollection(mentionedMessagesCollection, object, type);
        System.out.println("Got into mentioned messages");
    }

    private void updateStarredMessages(JSONObject object, RPC.MsgType type) {
        updateMessageCollection(starredMessagesCollection, object, type);
        System.out.println("Got into starred messages");
    }

    private void updatePinnedMessages(JSONObject object, RPC.MsgType type) {
        updateMessageCollection(pinnedMessagesCollection, object, type);
        System.out.println("Got into pinned messages");
    }

    private void updateSnipettedMessages(JSONObject object, RPC.MsgType type) {
        updateMessageCollection(snipetedMessagesCollection, object, type);

        System.out.println("Got into snipetted messages");
    }


    private void updateMessageCollection(StreamCollectionListener<MessageDocument> collectionListener, JSONObject object, RPC.MsgType type) {
        String id = object.optString("id");

        if (collectionListener != null) {
            switch (type) {
                case ADDED:
                    MessageDocument document = null;
                    try {
                        try {
                            document = new MessageDocument(getMessageDocumentAdapter().fromJson(object.optJSONObject("fields").put("_id", id).toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    collectionListener.onAdded(id, document);

                    break;
                case CHANGED:

                    MessageDocument changedDocument = null;
                    try {
                        changedDocument = new MessageDocument(getMessageDocumentAdapter().fromJson(object.optJSONObject("fields").put("_id", id).toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    collectionListener.onChanged(id, changedDocument);
                    break;
                case REMOVED:
                    collectionListener.onRemoved(id);
                    break;
            }
        }
    }

    public enum Type {
        STREAM_COLLECTION,
        LOCAL_COLLECTION
    }

    public static Type getCollectionType(JSONObject object) {
        String collectionName = object.optString("collection");
        if (collectionName.equals(COLLECTION_TYPE_FILES) ||
                collectionName.equals(COLLECTION_TYPE_MENTIONED_MESSAGES) ||
                collectionName.equals(COLLECTION_TYPE_STARRED_MESSAGES) ||
                collectionName.equals(COLLECTION_TYPE_PINNED_MESSAGES) ||
                collectionName.equals(COLLECTION_TYPE_SNIPETED_MESSAGES)) {
            return Type.LOCAL_COLLECTION;
        } else {
            return Type.STREAM_COLLECTION;
        }
    }

    private JsonAdapter<Message> messageDocumentAdapter;

    private JsonAdapter<Message> getMessageDocumentAdapter() {
        if (messageDocumentAdapter == null) {
            messageDocumentAdapter = moshi.adapter(Message.class);
        }
        return messageDocumentAdapter;
    }
}
