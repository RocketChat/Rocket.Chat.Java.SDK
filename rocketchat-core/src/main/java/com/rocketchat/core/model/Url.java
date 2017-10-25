package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import javax.annotation.Nullable;

/**
 * Created by sachin on 22/8/17.
 */
@AutoValue
public abstract class Url {
    public abstract String url();

    @Nullable
    public abstract Boolean ignoreParse();

    @Nullable
    public abstract UrlMeta meta();

    @Nullable
    public abstract ParsedUrl parsedUrl();

    public static Url create(String url, Boolean ignoreParse, UrlMeta meta, ParsedUrl parsedUrl) {
        return new AutoValue_Url(url, ignoreParse, meta, parsedUrl);
    }

    public static JsonAdapter<Url> jsonAdapter(Moshi moshi) {
        return new AutoValue_Url.MoshiJsonAdapter(moshi);
    }
}
