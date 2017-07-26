package io.rocketchat.core.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sachin on 26/7/17.
 */
public class Permission {
    String id;
    ArrayList <String> roles;
    Date updatedAt;
    MetaData metaData;
    Integer loki;

    public Permission(JSONObject object){
        try {
            id= object.getString("_id");
            roles=new ArrayList<>();
            JSONArray array= object.getJSONArray("roles");
            for (int i=0;i<array.length();i++){
                roles.add(array.getString(i));
            }
            updatedAt= new Date( object.getJSONObject("_updatedAt").getInt("$date"));
            metaData= new MetaData(object.getJSONObject("meta"));
            loki= object.optInt("$loki");
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
