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
    String visitorToken;
    String senderAlias;
    Date msgTimestamp;
    JSONObject sender;
    Date updatedAt;  //Message saved on the server
    Boolean newRoom;
    Boolean showConnecting; //This message triggers showconnecting popup
    String sandstormSessionId;
    Date editedAt;
    JSONObject editedBy;

    public MessageObject(JSONObject object){
        try {
            messageId=object.optString("_id");
            roomId=object.optString("rid");
            message=object.optString("msg");
            visitorToken=object.getString("token");
            senderAlias=object.getString("alias");
            if (object.optJSONObject("ts")!=null) {
                msgTimestamp = new Date(new Timestamp(object.getJSONObject("ts").getLong("$date")).getTime());
            }
            sender=object.optJSONObject("u");
            updatedAt = new Date(new Timestamp(object.getJSONObject("_updatedAt").getLong("$date")).getTime());
            newRoom=object.getBoolean("newRoom");
            showConnecting=object.getBoolean("showConnecting");
            sandstormSessionId=object.getString("sandstormSessionId");
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
