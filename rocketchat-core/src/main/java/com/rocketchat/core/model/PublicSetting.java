package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.AsString;
import com.rocketchat.common.data.ISO8601Date;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.Date;
import org.json.JSONObject;

import javax.annotation.Nullable;

@AutoValue
public abstract class PublicSetting {

    @Json(name = "_id")
    public abstract String id();

    @Nullable
    public abstract String group();

    @Nullable
    public abstract String type();

    @AsString
    @Nullable
    public abstract String value();

    @Json(name = "_updatedAt")
    public abstract @ISO8601Date Long updatedAt();

    public boolean valueAsBoolean() {
        return Boolean.parseBoolean(value());
    }

    public long valueAsLong() {
        return Long.parseLong(value());
    }

    public static JsonAdapter<PublicSetting> jsonAdapter(Moshi moshi) {
        return new AutoValue_PublicSetting.MoshiJsonAdapter(moshi);
    }
}
