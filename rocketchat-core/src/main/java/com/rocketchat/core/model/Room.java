package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.ISO8601Date;
import com.rocketchat.common.data.model.BaseRoom;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by sachin on 19/7/17.
 */
@AutoValue
public abstract class Room extends BaseRoom {

    @Nullable
    public abstract String topic();

    @Json(name = "muted")
    @Nullable
    public abstract List<String> mutedUsers();

    @Nullable
    public abstract @ISO8601Date
    Long jitsiTimeout();

    @Json(name = "ro")
    @Nullable
    public abstract Boolean readOnly();

    public static JsonAdapter<Room> jsonAdapter(Moshi moshi) {
        return new AutoValue_Room.MoshiJsonAdapter(moshi);
    }
}
