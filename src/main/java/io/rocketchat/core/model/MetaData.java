package io.rocketchat.core.model;

import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 26/7/17.
 */
public class MetaData {
    private Integer revision;
    private Date created;
    private Integer version;
    private Date updated;

    public MetaData(JSONObject object) {
        try {
            revision = object.getInt("revision");
            created = new Date(object.getInt("created"));
            version = object.getInt("version");
            if (object.opt("updated") != null) {
                updated = new Date(object.getInt("updated"));
            }
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
