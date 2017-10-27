package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.AsString;
import com.rocketchat.common.data.ISO8601Date;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;

@AutoValue
public abstract class Setting {

    @Json(name = "_id")
    public abstract String id();

    @AsString
    @Nullable
    public abstract String value();

    public boolean valueAsBoolean() {
        return Boolean.parseBoolean(value());
    }

    public long valueAsLong() {
        return Long.parseLong(value());
    }

    public static JsonAdapter<Setting> jsonAdapter(Moshi moshi) {
        return new AutoValue_Setting.MoshiJsonAdapter(moshi);
    }


}
