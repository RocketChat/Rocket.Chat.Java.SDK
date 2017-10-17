package com.rocketchat.common.data.lightdb.document;

import org.json.JSONObject;

public class LoginConfDocument {
    String service;
    String clientId;
    String appId;
    String consumerKey;

    public LoginConfDocument(JSONObject conf) {
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

    public String getAppId() {
        return appId;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void update(JSONObject object) {
        if (object.opt("service") != null) {
            service = object.optString("service");
        }
        if (object.opt("clientId") != null) {
            clientId = object.optString("clientId");
        }
        if (object.opt("appId") != null) {
            appId = object.optString("appId");
        }
        if (object.opt("consumerKey") != null) {
            consumerKey = object.optString("consumerKey");
        }
    }

    @Override
    public String toString() {
        return "LoginConfDocument{" +
                "service='" + service + '\'' +
                ", clientId='" + clientId + '\'' +
                ", appId='" + appId + '\'' +
                ", consumerKey='" + consumerKey + '\'' +
                '}';
    }
}
