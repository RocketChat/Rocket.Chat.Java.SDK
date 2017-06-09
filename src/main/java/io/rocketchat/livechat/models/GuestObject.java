package io.rocketchat.livechat.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by sachin on 9/6/17.
 */
public class GuestObject {

    String userID;
    String token;
    Date tokenExpiry;

    public GuestObject(JSONObject object){
        try {
            userID= object.optString("userId");
            token=object.getString("token");
            if (object.optJSONObject("tokenExpires")!=null) {
                tokenExpiry = new Date(new Timestamp(object.getJSONObject("tokenExpires").getLong("$date")).getTime());
            }
            if (object.optString("id")!=null) {
                userID = object.optString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Date tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    @Override
    public String toString() {
        return "GuestObject{" +
                "userID='" + userID + '\'' +
                ", token='" + token + '\'' +
                ", tokenExpiry=" + tokenExpiry +
                '}';
    }
}
