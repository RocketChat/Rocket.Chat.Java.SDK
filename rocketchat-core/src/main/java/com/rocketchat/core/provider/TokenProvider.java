package com.rocketchat.core.provider;

import com.rocketchat.core.model.TokenObject;

public interface TokenProvider {
    void saveToken(TokenObject token);

    TokenObject getToken(String userId);
}
