package com.rocketchat.common.data.lightdb.document;

import org.json.JSONObject;

public class LoginConfDocument {
    String service;
    String clientId;
    String appId;
    String consumerKey;

    LoginConfDocument(JSONObject conf) {
        service = conf.optString("service");
        clientId = conf.optString("clientId");
        appId = conf.optString("appId");
        consumerKey = conf.optString("consumerKey");
    }

    public String getService() {
        return service;
    }

    public String getClientId() {
        return clientId;
    }
}
