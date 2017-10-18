package com.rocketchat.common.data.model.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

@AutoValue
public abstract class TypedResponse<T> {
    public abstract T result();

    public static <T> JsonAdapter<TypedResponse<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_TypedResponse.MoshiJsonAdapter<>(moshi, types);
    }
}
