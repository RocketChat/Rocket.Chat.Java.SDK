package com.rocketchat.common.listener;

import com.rocketchat.common.RocketChatException;

import java.lang.reflect.Type;

public abstract class Callback {
    public abstract void onError(RocketChatException error);

    public abstract Type getClassType();
}
