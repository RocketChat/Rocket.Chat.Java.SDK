package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.ISO8601Date;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

import javax.annotation.Nullable;

@AutoValue
public abstract class Permission {

    @Json(name = "_id")
    public abstract String id();

    public abstract List<String> roles();

    public abstract MetaData meta();

    @Json(name = "_updatedAt")
    @Nullable
    public abstract @ISO8601Date Long updatedAt();

    @Json(name = "$loki")
    public abstract Integer loki();

    public static JsonAdapter<Permission> jsonAdapter(Moshi moshi) {
        return new AutoValue_Permission.MoshiJsonAdapter(moshi);
    }
}
