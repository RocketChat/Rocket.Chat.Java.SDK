package io.rocketchat.common.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sachin on 12/6/17.
 */
public class UserObject {
    String userId;
    String userName;
    ArrayList <String> roles;
    JSONArray emails;

    public UserObject(JSONObject object){
        try {
            userId=object.getString("_id");
            userName=object.getString("username");
            if (object.opt("roles")!=null){
                roles=new ArrayList<>();
                JSONArray array= object.optJSONArray("roles");
                for (int i=0;i<array.length();i++){
                    roles.add(array.getString(i));
                }
            }
            if (object.opt("emails")!=null){
                emails=object.optJSONArray("emails");
            }
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

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public JSONArray getEmails() {
        return emails;
    }

    @Override
    public String toString() {
        return "UserObject{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", roles=" + roles +
                ", emails=" + emails +
                '}';
    }
}
