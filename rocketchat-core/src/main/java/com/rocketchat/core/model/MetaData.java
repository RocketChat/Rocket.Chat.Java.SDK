package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;

@AutoValue
public abstract class MetaData {

    public abstract int revision();

    public abstract long created();

    public abstract int version();

    @Nullable
    public abstract Long updated();

    public static JsonAdapter<MetaData> jsonAdapter(Moshi moshi) {
        return new AutoValue_MetaData.MoshiJsonAdapter(moshi);
    }
}
