package io.rocketchat.livechat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */
public class AgentObject {
    String name;
    String username;
    JSONArray emails;
    String agentId;

    public AgentObject(JSONObject object){
        try {
            agentId=object.getString("_id");
            name=object.getString("name");
            username=object.getString("username");
            emails=object.getJSONArray("emails");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JSONArray getEmails() {
        return emails;
    }

    public void setEmails(JSONArray emails) {
        this.emails = emails;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String toString() {
        return "AgentObject{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", emails=" + emails +
                ", agentId='" + agentId + '\'' +
                '}';
    }
}
