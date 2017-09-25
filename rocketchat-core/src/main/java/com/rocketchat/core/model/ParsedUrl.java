package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

@AutoValue
public abstract class ParsedUrl {
    @Nullable public abstract String host();
    @Nullable public abstract String hash();
    @Nullable public abstract String pathname();
    @Nullable public abstract String protocol();
    @Nullable public abstract String port();
    @Nullable public abstract String query();
    @Nullable public abstract String search();
    @Nullable public abstract String hostname();

    public static JsonAdapter<ParsedUrl> jsonAdapter(Moshi moshi) {
        return new AutoValue_ParsedUrl.MoshiJsonAdapter(moshi);
    }
}
