package com.rocketchat.common.data.model.internal;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.model.MessageType;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;

@AutoValue
public abstract class SocketMessage {
    @Json(name = "msg")
    public abstract MessageType messageType();

    @Nullable
    public abstract String id();

    public static JsonAdapter<SocketMessage> jsonAdapter(Moshi moshi) {
        return new AutoValue_SocketMessage.MoshiJsonAdapter(moshi);
    }
}
