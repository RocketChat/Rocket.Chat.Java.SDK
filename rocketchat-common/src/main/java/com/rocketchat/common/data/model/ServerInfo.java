package com.rocketchat.common.data.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class ServerInfo {
    public abstract String version();

    public static JsonAdapter<ServerInfo> jsonAdapter(Moshi moshi) {
        return new AutoValue_ServerInfo.MoshiJsonAdapter(moshi);
    }
}
