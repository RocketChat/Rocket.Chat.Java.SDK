package com.rocketchat.core.model;

import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 26/7/17.
 */
public class Permission {
    private String id;
    private ArrayList<String> roles;
    private Date updatedAt;
    private MetaData metaData;
    private Integer loki;

    public Permission(JSONObject object) {
        try {
            id = object.getString("_id");
            roles = new ArrayList<>();
            JSONArray array = object.getJSONArray("roles");
            for (int i = 0; i < array.length(); i++) {
                roles.add(array.getString(i));
            }
            if (object.opt("_updatedAt") != null) {
                updatedAt = new Date(object.getJSONObject("_updatedAt").getInt("$date"));
            }
            metaData = new MetaData(object.getJSONObject("meta"));
            loki = object.optInt("$loki");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public Integer getLoki() {
        return loki;
    }
}
