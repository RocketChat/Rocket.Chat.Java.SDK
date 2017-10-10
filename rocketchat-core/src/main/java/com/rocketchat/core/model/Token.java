package com.rocketchat.core.model;

import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 18/7/17.
 */
public class Token {

    private String userId;
    private String authToken;
    private Date expiry;

    public Token(String userId, String authToken, Date expiry) {
        this.userId = userId;
        this.authToken = authToken;
        this.expiry = expiry;
    }

    public Token(JSONObject object) throws JSONException {
        userId = object.getString("id");
        authToken = object.getString("token");
        JSONObject expires = object.optJSONObject("tokenExpires");
        if (expires != null) {
            long date = expires.optLong("$date");
            expiry = new Date(date);
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Date getExpiry() {
        return expiry;
    }

    @Override
    public String toString() {
        return "TokenObject{" +
                "userId='" + userId + '\'' +
                ", authToken='" + authToken + '\'' +
                ", expiry=" + expiry +
                '}';
    }
}
