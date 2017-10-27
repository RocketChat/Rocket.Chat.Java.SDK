package com.rocketchat.common.data.model.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;
import java.util.List;

@AutoValue
public abstract class TypedListResponse<T> {
    public abstract List<T> result();

    public static <T> JsonAdapter<TypedListResponse<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_TypedListResponse.MoshiJsonAdapter<>(moshi, types);
    }
}
