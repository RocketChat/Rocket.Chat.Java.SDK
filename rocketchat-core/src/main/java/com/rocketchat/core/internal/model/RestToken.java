package com.rocketchat.core.internal.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.core.model.Token;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class RestToken {

    public abstract String userId();
    public abstract String authToken();

    public static RestToken create(String userId, String authToken) {
        return new AutoValue_RestToken(userId, authToken);
    }

    public static JsonAdapter<RestToken> jsonAdapter(Moshi moshi) {
        return new AutoValue_RestToken.MoshiJsonAdapter(moshi);
    }

    public Token toToken() {
        return Token.create(userId(), authToken());
    }
}
