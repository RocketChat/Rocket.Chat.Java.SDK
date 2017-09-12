package com.rocketchat.core.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 18/7/17.
 */
public class TokenObject {

    private String userId;
    private String AuthToken;
    private Date Expiry;

    public TokenObject(String userId, String authToken, Date expiry) {
        this.userId = userId;
        AuthToken = authToken;
        Expiry = expiry;
    }

    public TokenObject(JSONObject object) {
        try {
            userId = object.optString("id");
            AuthToken = object.optString("token");
            Expiry = new Date(object.optJSONObject("tokenExpires").getLong("$date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public Date getExpiry() {
        return Expiry;
    }

    @Override
    public String toString() {
        return "TokenObject{" +
                "userId='" + userId + '\'' +
                ", AuthToken='" + AuthToken + '\'' +
                ", Expiry=" + Expiry +
                '}';
    }
}
