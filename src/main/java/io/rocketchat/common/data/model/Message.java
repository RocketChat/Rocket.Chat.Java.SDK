package io.rocketchat.common.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 19/7/17.
 */

public class Message {

    private String messageId;
    protected String roomId;
    protected String message;
    private Date msgTimestamp;
    private UserObject sender;
    private Date updatedAt;  //Message saved on the server
    private Date editedAt;
    private UserObject editedBy;
    private String messagetype;
    private String senderAlias;


    private static final String TYPE_MESSAGE_REMOVED = "rm";
    private static final String TYPE_ROOM_NAME_CHANGED = "r";
    private static final String TYPE_ROOM_ARCHIVED = "room-archived";
    private static final String TYPE_ROOM_UNARCHIVED = "room-unarchived";
    private static final String TYPE_USER_ADDED = "au";
    private static final String TYPE_USER_REMOVED = "ru";
    private static final String TYPE_USER_JOINED = "uj";
    private static final String TYPE_USER_LEFT = "ul";
    private static final String TYPE_USER_MUTED = "user-muted";
    private static final String TYPE_USER_UNMUTED = "user-unmuted";
    private static final String TYPE_WELCOME = "wm";
    private static final String TYPE_SUBSCRIPTION_ROLE_ADDED = "subscription-role-added";
    private static final String TYPE_SUBSCRIPTION_ROLE_REMOVED = "subscription-role-removed";



    public Message(JSONObject object) {
        try {
            messageId = object.optString("_id");
            roomId = object.optString("rid");
            message = object.optString("msg");
            if (object.optJSONObject("ts") != null) {
                msgTimestamp = new Date(object.getJSONObject("ts").getLong("$date"));
            }
            sender = new UserObject(object.optJSONObject("u"));
            updatedAt = new Date(object.getJSONObject("_updatedAt").getLong("$date"));

            if (object.optJSONObject("editedAt") != null) {
                editedAt = new Date(object.getJSONObject("editedAt").getLong("$date"));
                editedBy = new UserObject(object.getJSONObject("editedBy"));
            }
            messagetype = object.optString("t");
            senderAlias = object.optString("alias");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getMessageId() {
        return messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getMessage() {
        return message;
    }

    public Date getMsgTimestamp() {
        return msgTimestamp;
    }

    public UserObject getSender() {
        return sender;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public UserObject getEditedBy() {
        return editedBy;
    }

    public String getMessagetype() {
        return messagetype;
    }

    public String getSenderAlias() {
        return senderAlias;
    }

    enum Type {
        TEXT,
        TEXT_ATTACHMENT,
        IMAGE,
        AUDIO,
        VIDEO,
        URL,
        MESSAGE_REMOVED,
        ROOM_NAME_CHANGED,
        ROOM_ARCHIVED,
        ROOM_UNARCHIVED,
        USER_ADDED,
        USER_REMOVED,
        USER_JOINED,
        USER_LEFT,
        USER_MUTED,
        USER_UNMUTED,
        WELCOME,
        SUBSCRIPTION_ROLE_ADDED,
        SUBSCRIPTION_ROLE_REMOVED,
        OTHER
    }

    public static Type getType(String s) {
        if (s.equals(TYPE_MESSAGE_REMOVED)) {
            return Type.MESSAGE_REMOVED;
        }else if (s.equals(TYPE_ROOM_NAME_CHANGED)) {
            return Type.ROOM_NAME_CHANGED;
        }else if (s.equals(TYPE_ROOM_ARCHIVED)) {
            return Type.ROOM_ARCHIVED;
        }else if (s.equals(TYPE_ROOM_UNARCHIVED)) {
            return Type.ROOM_UNARCHIVED;
        }else if (s.equals(TYPE_USER_ADDED)) {
            return Type.USER_ADDED;
        }else if (s.equals(TYPE_USER_REMOVED)) {
            return Type.USER_REMOVED;
        }else if (s.equals(TYPE_USER_JOINED)) {
            return Type.USER_JOINED;
        }else if (s.equals(TYPE_USER_LEFT)) {
            return Type.USER_LEFT;
        }else if (s.equals(TYPE_USER_MUTED)) {
            return Type.USER_MUTED;
        }else if (s.equals(TYPE_USER_UNMUTED)) {
            return Type.USER_UNMUTED;
        }else if (s.equals(TYPE_WELCOME)) {
            return Type.WELCOME;
        }else if (s.equals(TYPE_SUBSCRIPTION_ROLE_ADDED)) {
            return Type.SUBSCRIPTION_ROLE_ADDED;
        }else if (s.equals(TYPE_SUBSCRIPTION_ROLE_REMOVED)) {
            return Type.SUBSCRIPTION_ROLE_REMOVED;
        }
        return Type.OTHER;
    }

}
