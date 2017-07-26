package io.rocketchat.core.model;

import io.rocketchat.common.data.model.UserObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sachin on 27/7/17.
 */
public class RoomRole {
    String id;
    String roomId;
    UserObject user;
    ArrayList <String> roles;

    RoomRole(JSONObject object){
        try {
            id= object.getString("_id");
            roomId=object.getString("rid");
            user= new UserObject(object.getJSONObject("u"));
            roles=new ArrayList<>();
            JSONArray array= object.optJSONArray("roles");
            for (int i=0;i<array.length();i++){
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

    public UserObject getUser() {
        return user;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }
}
