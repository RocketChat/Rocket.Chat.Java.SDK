package com.rocketchat.common.data.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Created by sachin on 12/6/17.
 */
@AutoValue
public abstract class User extends BaseUser {

    public static JsonAdapter<User> jsonAdapter(Moshi moshi) {
        return new AutoValue_User.MoshiJsonAdapter(moshi);
    }

    public static Builder builder() {
        return new AutoValue_User.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseBuilder<Builder> {
        public abstract User build();
    }

    /*private String userId;
    protected String userName;
    protected ArrayList<String> roles;
    protected JSONArray emails;

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

    public String getAvatarUrl() {
        return Utils.getAvatar(userName);
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
        } else if (s.equals(OFFLINE)) {
            return Status.OFFLINE;
        } else if (s.equals(BUSY)) {
            return Status.BUSY;
        } else if (s.equals(AWAY)) {
            return Status.AWAY;
        }
        return Status.OTHER;
    }*/
}
