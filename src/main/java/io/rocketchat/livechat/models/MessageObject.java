package io.rocketchat.livechat.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by sachin on 9/6/17.
 */
public class MessageObject {
    String messageId;
    String roomId;
    String message;
    Date msgTimestamp;
    JSONObject sender;
    Date updatedAt;  //Message saved on the server
    Date editedAt;
    JSONObject editedBy;

    public MessageObject(JSONObject object){
        try {
            messageId=object.optString("_id");
            roomId=object.optString("rid");
            message=object.optString("msg");
            if (object.optJSONObject("ts")!=null) {
                msgTimestamp = new Date(new Timestamp(object.getJSONObject("ts").getLong("$date")).getTime());
            }
            sender=object.optJSONObject("u");

            if (object.optJSONObject("editedAt")!=null) {
                editedAt = new Date(new Timestamp(object.getJSONObject("editedAt").getLong("$date")).getTime());
                editedBy = object.getJSONObject("editedBy");
            }

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

    public JSONObject getSender() {
        return sender;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public JSONObject getEditedBy() {
        return editedBy;
    }
}
