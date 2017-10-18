package com.rocketchat.common.utils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;

public class Json {

    public static <T> T parseJson(Moshi moshi, Type type, String message) throws IOException {
        JsonAdapter<T> adapter = moshi.adapter(type);
        return adapter.fromJson(message);
    }
}
