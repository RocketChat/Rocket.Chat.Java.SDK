package io.rocketchat.common.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 12/6/17.
 */
public class UserObject {
    String userId;
    String userName;
    public UserObject(JSONObject object){
        try {
            userId=object.getString("_id");
            userName=object.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
