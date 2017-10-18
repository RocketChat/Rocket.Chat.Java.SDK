package com.rocketchat.common.data.lightstream.document;

import org.json.JSONObject;

public class ClientVersionsDocument {

    String id;
    String version;
    Boolean current = false;

    public ClientVersionsDocument(JSONObject object) {
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

    public void update(JSONObject object) {
        if (object.opt("version") != null) {
            version = object.optString("version");
        }

        if (object.opt("current") != null) {
            current = object.optBoolean("current");
        }
    }

    @Override
    public String toString() {
        return "ClientVersionsDocument{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", current=" + current +
                '}';
    }
}
