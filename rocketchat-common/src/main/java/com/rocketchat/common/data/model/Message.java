package com.rocketchat.common.data.model;

import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 19/7/17.
 */

public class Message {

    protected String messageId;
    protected String roomId;
    protected String message;
    protected Date msgTimestamp;
    protected UserObject sender;
    protected Date updatedAt;  //Message saved on the server
    protected Date editedAt;
    protected UserObject editedBy;
    protected String messagetype;
    protected String senderAlias;

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

}
