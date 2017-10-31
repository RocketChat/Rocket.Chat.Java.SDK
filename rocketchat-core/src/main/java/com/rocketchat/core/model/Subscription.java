package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.ISO8601Date;
import com.rocketchat.common.data.model.BaseRoom;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Created by sachin on 19/7/17.
 */
@AutoValue
public abstract class Subscription extends BaseRoom {

    @Json(name = "ts")
    @Nullable
    public abstract @ISO8601Date
    Long timestamp();

    @Json(name = "ls")
    @Nullable
    public abstract @ISO8601Date
    Long lastSeen();

    @Nullable
    public abstract Boolean open();

    @Nullable
    public abstract Boolean alert();

    @Nullable
    public abstract Integer unread();

    @Json(name = "f")
    @Nullable
    public abstract Boolean favourite();

    @Json(name = "_updatedAt")
    @Nullable
    public abstract @ISO8601Date
    Long updatedAt();

    @Nullable
    public abstract String desktopNotifications();

    @Nullable
    public abstract String mobilePushNotifications();

    @Nullable
    public abstract String emailNotifications();


    // TODO: 10/10/17 subscriptionId = object.getString("_id");
    // TODO: 10/10/17 favourite = object.getBoolean("f");
    // TODO: 10/10/17  blocked = object.optBoolean("blocked");
    // TODO: 10/10/17 fullname = object.optString("fname");
    // TODO: 10/10/17 lastActivity = new Date(object.getJSONObject("lastActivity").getLong("$date"));

    public static JsonAdapter<Subscription> jsonAdapter(Moshi moshi) {
        return new AutoValue_Subscription.MoshiJsonAdapter(moshi);
    }
}