package com.rocketchat.common.data.lightstream.document;

import java.util.Date;
import org.json.JSONObject;

// TODO: 19/10/17 Add autovalue
public class RocketChatRolesDocument {
    String name;
    String scope;
    String description;
    Boolean isProtected;
    Date updatedAt;

    public RocketChatRolesDocument(JSONObject roles) {
        name = roles.optString("name");
        scope = roles.optString("scope");
        description = roles.optString("description");
        isProtected = roles.optBoolean("protected");
        if (roles.opt("_updatedAt") != null) {
            updatedAt = new Date(roles.optJSONObject("_updatedAt").optLong("$date"));
        }
    }


    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isProtected() {
        return isProtected;
    }

    public void setProtected(Boolean _protected) {
        this.isProtected = _protected;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void update(JSONObject object) {
        if (object.opt("name") != null) {
            name = object.optString("name");
        }
        if (object.opt("scope") != null) {
            scope = object.optString("scope");
        }
        if (object.opt("description") != null) {
            description = object.optString("description");
        }
        if (object.opt("protected") != null) {
            isProtected = object.optBoolean("protected");
        }
        if (object.opt("_updatedAt") != null) {
            updatedAt = new Date(object.optJSONObject("_updatedAt").optLong("$date"));
        }
    }

    @Override
    public String toString() {
        return "RocketChatRolesDocument{" +
                "name='" + name + '\'' +
                ", scope='" + scope + '\'' +
                ", description='" + description + '\'' +
                ", isProtected=" + isProtected +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
