package com.rocketchat.common.data.lightdb.document;

import org.json.JSONObject;

public class ClientVersionsDocument {

    String id;
    String version;
    Boolean current = false;

    ClientVersionsDocument (JSONObject object) {
        version = object.optString("version");
        current = object.optBoolean("current");
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public Boolean isCurrent() {
        return current;
    }
}
