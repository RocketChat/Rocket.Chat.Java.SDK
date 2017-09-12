package com.rocketchat.common.data.lightdb.document;

import org.json.JSONObject;

public class LoginConfDocument {
    String service;
    String clientId;

    LoginConfDocument(JSONObject conf) {
        service = conf.optString("service");
        clientId = conf.optString("clientId");
    }

    public String getService() {
        return service;
    }

    public String getClientId() {
        return clientId;
    }
}
