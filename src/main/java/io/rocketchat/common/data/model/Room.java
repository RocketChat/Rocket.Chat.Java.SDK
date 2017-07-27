package io.rocketchat.common.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 22/7/17.
 */

public class Room {

    protected String roomId;
    protected String roomType;
    protected String roomName;
    protected UserObject userInfo;

    public Room(JSONObject object){
        try {
            roomId = object.getString("_id");
            roomType = object.getString("t");
            roomName = object.optString("name");
            if (object.optJSONObject("u")!=null){
                userInfo=new UserObject(object.optJSONObject("u"));
            }
        } catch (JSONException e) {
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

    public UserObject getUserInfo() {
        return userInfo;
    }
}
