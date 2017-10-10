package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;

@AutoValue
public abstract class UrlMeta {
    @Nullable public abstract String pageTitle();
    @Nullable public abstract String fbAppId();
    @Nullable public abstract String description();
    @Nullable public abstract String ogImage();
    @Nullable public abstract String ogSiteName();
    @Nullable public abstract String ogType();
    @Nullable public abstract String ogTitle();
    @Nullable public abstract String ogUrl();
    @Nullable public abstract String ogDescription();

    public static JsonAdapter<UrlMeta> jsonAdapter(Moshi moshi) {
        return new AutoValue_UrlMeta.MoshiJsonAdapter(moshi);
    }
}
