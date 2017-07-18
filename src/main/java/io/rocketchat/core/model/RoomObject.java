package io.rocketchat.core.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 19/7/17.
 */

public class RoomObject {
    String roomId;
    String roomType;
    String roomName;
    JSONObject userInfo;
    String topic;
    JSONArray mutedUsers;
    Date jitsiTimeout;
    Boolean readOnly;

    public RoomObject(JSONObject object){
        try {
            roomId = object.getString("_id");
            roomType = object.getString("t");
            roomName = object.optString("name");
            userInfo = object.optJSONObject("u");
            topic = object.optString("topic");
            mutedUsers = object.optJSONArray("muted");
            if (object.optJSONObject("jitsiTimeout")!=null) {
                jitsiTimeout = new Date(object.getJSONObject("jitsiTimeout").getLong("$date"));
            }
            readOnly = object.optBoolean("ro");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public JSONObject getUserInfo() {
        return userInfo;
    }

    public String getTopic() {
        return topic;
    }

    public JSONArray getMutedUsers() {
        return mutedUsers;
    }

    public Date getJitsiTimeout() {
        return jitsiTimeout;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }
}
