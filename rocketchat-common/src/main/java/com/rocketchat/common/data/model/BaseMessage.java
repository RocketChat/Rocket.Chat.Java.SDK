package com.rocketchat.common.data.model;

import com.rocketchat.common.data.Timestamp;
import com.squareup.moshi.Json;

import javax.annotation.Nullable;

/**
 * Created by sachin on 19/7/17.
 */

public abstract class BaseMessage {

    /*private String id;
    protected String roomId;
    protected String message;
    private Date timestamp;
    private UserObject sender;
    private Date updatedAt;  //Message saved on the server
    private Date editedAt;
    private UserObject editedBy;
    private String messagetype;
    private String senderAlias;

    public Message(JSONObject object) {
        try {
            id = object.optString("_id");
            roomId = object.optString("rid");
            message = object.optString("msg");
            if (object.optJSONObject("ts") != null) {
                timestamp = new Date(object.getJSONObject("ts").getLong("$date"));
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

    }*/

    @Json(name = "_id") public abstract String id();
    @Json(name = "rid") public abstract String roomId();
    @Json(name = "msg") public abstract String message();
    @Json(name = "ts") public abstract @Timestamp Long timestamp();
    @Nullable public abstract User sender();
    @Json(name = "_updatedAt") public abstract @Timestamp Long updatedAt();
    @Nullable public abstract @Timestamp Long editedAt();
    @Nullable public abstract User editedBy();
    // TODO - use an ENUM
    @Json(name = "t") @Nullable public abstract String type();
    @Json(name = "alias") @Nullable public abstract String senderAlias();

    public abstract static class Builder<T extends Builder<T>> {
        public abstract T setId(String id);
        public abstract T setRoomId(String id);
        public abstract T setMessage(String message);
        public abstract T setTimestamp(Long timestamp);
        public abstract T setSender(User sender);
        public abstract T setUpdatedAt(Long updatedAt);
        public abstract T setEditedAt(Long editedAt);
        public abstract T setEditedBy(User editedBy);
        public abstract T setType(String type);
        public abstract T setSenderAlias(String alias);
    }
}
