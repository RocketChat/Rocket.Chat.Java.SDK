package com.rocketchat.common.data.model.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class ConnectedMessage {
    public abstract String session();

    public static JsonAdapter<ConnectedMessage> jsonAdapter(Moshi moshi) {
        return new AutoValue_ConnectedMessage.MoshiJsonAdapter(moshi);
    }
}
