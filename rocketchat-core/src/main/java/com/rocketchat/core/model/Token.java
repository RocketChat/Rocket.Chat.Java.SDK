package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.Timestamp;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;

@AutoValue
public abstract class Token {

    @Json(name = "id") public abstract String userId();
    @Json(name = "token") public abstract String authToken();
    @Json(name = "tokenExpires") @Nullable public abstract @Timestamp Long expiresAt();

    public static Token create(String userId, String authToken) {
        return create(userId, authToken, null);
    }

    public static Token create(String userId, String authToken, Long expiresAt) {
        return new AutoValue_Token(userId, authToken, expiresAt);
    }

    public static JsonAdapter<Token> jsonAdapter(Moshi moshi) {
        return new AutoValue_Token.MoshiJsonAdapter(moshi);
    }
}
