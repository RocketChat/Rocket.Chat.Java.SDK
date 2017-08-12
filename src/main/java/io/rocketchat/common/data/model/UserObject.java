package io.rocketchat.common.data.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sachin on 12/6/17.
 */
public class UserObject {
    private String userId;
    private String userName;
    private ArrayList<String> roles;
    private JSONArray emails;


    // Extra fields can be null
    private String name;
    private Status status;
    private Integer utcOffset;

    public UserObject(JSONObject object) {
        try {
            userId = object.optString("_id");
            userName = object.getString("username");
            if (object.opt("roles") != null) {
                roles = new ArrayList<>();
                JSONArray array = object.optJSONArray("roles");
                for (int i = 0; i < array.length(); i++) {
                    roles.add(array.getString(i));
                }
            }
            if (object.opt("emails") != null) {
                emails = object.optJSONArray("emails");
            }

            name = object.optString("name");
            status = getStatus(object.optString("status"));
            utcOffset = object.optInt("utcOffset");

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

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getUtcOffset() {
        return utcOffset;
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


    public enum Status {
        ONLINE,
        BUSY,
        AWAY,
        OFFLINE,
        OTHER
    }

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final String BUSY = "busy";
    public static final String AWAY = "away";

    public static Status getStatus(String s) {
        if (s.equals(ONLINE)) {
            return Status.ONLINE;
        }else if (s.equals(OFFLINE)) {
            return Status.OFFLINE;
        }else if (s.equals(BUSY)) {
            return Status.BUSY;
        }else if (s.equals(AWAY)){
            return Status.AWAY;
        }
        return Status.OTHER;
    }
}
