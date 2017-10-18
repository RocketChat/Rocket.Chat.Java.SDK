package com.rocketchat.core.model;

import com.rocketchat.common.data.model.User;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 27/7/17.
 */
public class RoomRole {
    private String id;
    private String roomId;
    private User user;
    private ArrayList<String> roles;

    public RoomRole(JSONObject object) {
        try {
            id = object.getString("_id");
            roomId = object.getString("rid");
            // TODO user = new UserObject(object.getJSONObject("u"));
            roles = new ArrayList<>();
            JSONArray array = object.optJSONArray("roles");
            for (int i = 0; i < array.length(); i++) {
                roles.add(array.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }
}
