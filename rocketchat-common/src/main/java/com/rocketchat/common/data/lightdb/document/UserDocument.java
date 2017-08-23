package com.rocketchat.common.data.lightdb.document;

import com.rocketchat.common.data.model.UserObject;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sachin on 13/8/17.
 */
public class UserDocument extends UserObject {

    Boolean active;
    private String name;
    private JSONObject services;
    private Status status;
    private Status statusConnection;
    private Status statusDefault;
    private Integer utcOffset;

    public UserDocument(JSONObject object) {

        super(object);

        try {
            active = object.optBoolean("active");
            name = object.optString("name");
            services = object.optJSONObject("services");
            status = UserObject.getStatus(object.optString("status"));
            statusConnection = UserObject.getStatus(object.optString("statusConnection"));
            statusDefault = UserObject.getStatus(object.optString("statusDefault"));
            utcOffset = object.optInt("utcOffset");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public JSONObject getServices() {
        return services;
    }

    public Status getStatus() {
        return status;
    }

    public Status getStatusConnection() {
        return statusConnection;
    }

    public Status getStatusDefault() {
        return statusDefault;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void update(JSONObject object) {

        try {
            if (object.opt("username") != null) {
                userName = object.optString("username");
            }
            if (object.opt("roles") != null) {
                if (roles != null) {
                    roles.clear();
                } else {
                    roles = new ArrayList<>();
                }
                JSONArray array = object.optJSONArray("roles");
                for (int i = 0; i < array.length(); i++) {
                    roles.add(array.optString(i));
                }
            }
            if (object.opt("emails") != null) {
                emails = object.optJSONArray("emails");
            }
            if (object.opt("active") != null) {
                active = object.optBoolean("active");
            }
            if (object.opt("name") != null) {
                name = object.optString("name");
            }
            if (object.opt("services") != null) {
                services = object.optJSONObject("services");
            }
            if (object.opt("status") != null) {
                status = UserObject.getStatus(object.optString("status"));
            }
            if (object.opt("statusConnection") != null) {
                statusConnection = UserObject.getStatus(object.optString("statusConnection"));
            }
            if (object.opt("statusDefault") != null) {
                statusDefault = UserObject.getStatus(object.optString("statusDefault"));
            }
            if (object.opt("utcOffset") != null) {
                utcOffset = object.optInt("utcOffset");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "UserDocument{" +
                "active=" + active +
                ", name='" + name + '\'' +
                ", services=" + services +
                ", status=" + status +
                ", statusConnection=" + statusConnection +
                ", statusDefault=" + statusDefault +
                ", utcOffset=" + utcOffset +
                '}';
    }
}
