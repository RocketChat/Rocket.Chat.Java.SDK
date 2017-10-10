package com.rocketchat.core.model;

import com.rocketchat.common.data.model.Room;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 19/7/17.
 */

public class SubscriptionObject extends Room {

    private Date roomCreated;
    private Date lastSeen;
    private Boolean open;
    private Boolean alert;
    private Integer unread;
    private Date updatedAt;
    private String subscriptionId;
    private String fullname;
    private Boolean favourite = false;
    private boolean blocked = false;
    private Date lastActivity;

    private String desktopNotifications;
    private String mobilePushNotifications;
    private String emailNotifications;

    public SubscriptionObject(JSONObject object) {
        super(object);
        try {
            roomId = object.getString("rid");
            if (object.optJSONObject("ts") != null) {
                roomCreated = new Date(object.getJSONObject("ts").getLong("$date"));
            }
            if (object.optJSONObject("ls") != null) {
                lastSeen = new Date(object.getJSONObject("ls").getLong("$date"));
            }
            open = object.getBoolean("open");
            alert = object.getBoolean("alert");
            unread = object.getInt("unread");
            updatedAt = new Date(object.getJSONObject("_updatedAt").getLong("$date"));
            subscriptionId = object.getString("_id");

            desktopNotifications = object.optString("desktopNotifications");
            mobilePushNotifications = object.optString("mobilePushNotifications");
            emailNotifications = object.optString("emailNotifications");

            if (object.opt("f") != null) {
                favourite = object.getBoolean("f");
            }
            if (object.opt("blocked") != null) {
                blocked = object.optBoolean("blocked");
            }

            fullname = object.optString("fname");

            if (object.opt("lastActivity") != null) {
                lastActivity = new Date(object.getJSONObject("lastActivity").getLong("$date"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("name is " + getRoomName());
        }
    }

    public Date getRoomCreated() {
        return roomCreated;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public Boolean getOpen() {
        return open;
    }

    public Boolean getAlert() {
        return alert;
    }

    public Integer getUnread() {
        return unread;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getFullname() {
        return fullname;
    }

    public Boolean isFavourite() {
        return favourite;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    //    public String getDesktopNotifications() {
    //        return desktopNotifications;
    //    }
    //
    //    public String getMobilePushNotifications() {
    //        return mobilePushNotifications;
    //    }
    //
    //    public String getEmailNotifications() {
    //        return emailNotifications;
    //    }
}