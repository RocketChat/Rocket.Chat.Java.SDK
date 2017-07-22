package io.rocketchat.core.model;

import io.rocketchat.common.data.model.Room;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 19/7/17.
 */

public class RoomObject extends Room{

    String topic;
    JSONArray mutedUsers;
    Date jitsiTimeout;
    Boolean readOnly;

    public RoomObject(JSONObject object){
        super(object);
        try {
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
