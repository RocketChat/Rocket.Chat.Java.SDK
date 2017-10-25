package com.rocketchat.common.data.model;

import com.squareup.moshi.Json;
import javax.annotation.Nullable;

/**
 * Created by sachin on 22/7/17.
 */

public abstract class BaseRoom {

    /*protected String roomId;
    private String roomName;
    //TODO - private UserObject userInfo;
    private Type roomType;

    public Room(JSONObject object) {
        try {
            roomId = object.getString("_id");
            String type = object.getString("t");
            if (type.equals("d")) {
                roomType = Type.ONE_TO_ONE;
            } else if (type.equals("c")) {
                roomType = Type.PUBLIC;
            } else {
                roomType = Type.PRIVATE;
            }
            roomName = object.optString("name");
            *//*if (object.optJSONObject("u") != null) {
                userInfo = new UserObject(object.optJSONObject("u"));
            }*//*
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    @Json(name = "_id")
    public abstract String roomId();

    @Json(name = "t")
    @Nullable
    public abstract RoomType type();

    @Json(name = "u")
    @Nullable
    public abstract User user();

    @Nullable
    public abstract String name();

    public enum RoomType {
        @Json(name = "c") PUBLIC,
        @Json(name = "p") PRIVATE,
        @Json(name = "d") ONE_TO_ONE,
        @Json(name = "l") LIVECHAT
    }
}
