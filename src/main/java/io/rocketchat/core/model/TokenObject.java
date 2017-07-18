package io.rocketchat.core.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 18/7/17.
 */
public class TokenObject {

    String userId;
    String AuthToken;
    Date Expiry;

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
        }catch (JSONException e){
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
}
