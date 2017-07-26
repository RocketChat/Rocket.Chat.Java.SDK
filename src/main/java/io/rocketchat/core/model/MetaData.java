package io.rocketchat.core.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sachin on 26/7/17.
 */
public class MetaData {
    Integer revision;
    Date created;
    Integer version;
    Date updated;

    public MetaData(JSONObject object){
        try {
            revision=object.getInt("revision");
            created=new Date(object.getInt("created"));
            version=object.getInt("version");
            updated=new Date(object.getInt("updated"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getRevision() {
        return revision;
    }

    public Date getCreated() {
        return created;
    }

    public Integer getVersion() {
        return version;
    }

    public Date getUpdated() {
        return updated;
    }
}
