package io.rocketchat.livechat.model;

import io.rocketchat.common.data.model.UserObject;
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
    UserObject sender;
    Date updatedAt;  //Message saved on the server
    Boolean newRoom;
    Boolean showConnecting; //This message triggers showconnecting popup
    String sandstormSessionId;
    Date editedAt;
    UserObject editedBy;
    String messagetype;

    public MessageObject(JSONObject object){
        try {
            messageId=object.optString("_id");
            roomId=object.optString("rid");
            message=object.optString("msg");
            visitorToken=object.optString("token");
            senderAlias=object.optString("alias");
            if (object.optJSONObject("ts")!=null) {
                msgTimestamp = new Date(new Timestamp(object.getJSONObject("ts").getLong("$date")).getTime());
            }
            sender=new UserObject(object.optJSONObject("u"));
            updatedAt = new Date(new Timestamp(object.getJSONObject("_updatedAt").getLong("$date")).getTime());
            newRoom=object.optBoolean("newRoom");
            showConnecting=object.optBoolean("showConnecting");
            sandstormSessionId=object.optString("sandstormSessionId");
            if (object.optJSONObject("editedAt")!=null) {
                editedAt = new Date(new Timestamp(object.getJSONObject("editedAt").getLong("$date")).getTime());
                editedBy = new UserObject(object.getJSONObject("editedBy"));
            }

            messagetype=object.optString("t");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVisitorToken() {
        return visitorToken;
    }

    public void setVisitorToken(String visitorToken) {
        this.visitorToken = visitorToken;
    }

    public String getSenderAlias() {
        return senderAlias;
    }

    public void setSenderAlias(String senderAlias) {
        this.senderAlias = senderAlias;
    }

    public Date getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(Date msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getNewRoom() {
        return newRoom;
    }

    public void setNewRoom(Boolean newRoom) {
        this.newRoom = newRoom;
    }

    public Boolean getShowConnecting() {
        return showConnecting;
    }

    public void setShowConnecting(Boolean showConnecting) {
        this.showConnecting = showConnecting;
    }

    public String getSandstormSessionId() {
        return sandstormSessionId;
    }

    public void setSandstormSessionId(String sandstormSessionId) {
        this.sandstormSessionId = sandstormSessionId;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Date editedAt) {
        this.editedAt = editedAt;
    }

    public UserObject getSender() {
        return sender;
    }

    public void setSender(UserObject sender) {
        this.sender = sender;
    }

    public UserObject getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(UserObject editedBy) {
        this.editedBy = editedBy;
    }

    public String getMessagetype() {
        return messagetype;
    }

    public void setMessagetype(String messagetype) {
        this.messagetype = messagetype;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "messageId='" + messageId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", message='" + message + '\'' +
                ", visitorToken='" + visitorToken + '\'' +
                ", senderAlias='" + senderAlias + '\'' +
                ", msgTimestamp=" + msgTimestamp +
                ", sender=" + sender +
                ", updatedAt=" + updatedAt +
                ", newRoom=" + newRoom +
                ", showConnecting=" + showConnecting +
                ", sandstormSessionId='" + sandstormSessionId + '\'' +
                ", editedAt=" + editedAt +
                ", editedBy=" + editedBy +
                ", messagetype='" + messagetype + '\'' +
                '}';
    }
}
