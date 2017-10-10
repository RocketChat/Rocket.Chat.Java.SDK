package com.rocketchat.livechat.model;

import java.sql.Timestamp;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */

public class DepartmentObject {

    private String id;
    private Boolean enabled;
    private String deptName;
    private String description;
    private int numAgents;
    private Boolean showOnRegistration;
    private Date updatedAt;

    DepartmentObject(JSONObject object) {
        try {
            id = object.getString("_id");
            enabled = object.getBoolean("enabled");
            deptName = object.getString("name");
            description = object.getString("description");
            numAgents = object.getInt("numAgents");
            showOnRegistration = object.getBoolean("showOnRegistration");
            updatedAt = new Date(new Timestamp(object.getJSONObject("_updatedAt").getLong("$date")).getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumAgents() {
        return numAgents;
    }

    public void setNumAgents(int numAgents) {
        this.numAgents = numAgents;
    }

    public Boolean getShowOnRegistration() {
        return showOnRegistration;
    }

    public void setShowOnRegistration(Boolean showOnRegistration) {
        this.showOnRegistration = showOnRegistration;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DepartmentObject{" +
                "id='" + id + '\'' +
                ", enabled=" + enabled +
                ", deptName='" + deptName + '\'' +
                ", description='" + description + '\'' +
                ", numAgents=" + numAgents +
                ", showOnRegistration=" + showOnRegistration +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
