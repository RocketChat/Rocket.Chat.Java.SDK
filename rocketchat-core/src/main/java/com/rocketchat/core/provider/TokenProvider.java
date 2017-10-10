package com.rocketchat.core.provider;

import com.rocketchat.core.model.Token;

public interface TokenProvider {
    void saveToken(Token token);

    Token getToken();
}
