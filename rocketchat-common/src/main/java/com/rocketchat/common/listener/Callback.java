package com.rocketchat.common.listener;

import com.rocketchat.common.data.model.Error;

import java.lang.reflect.Type;

public abstract class Callback {
    public abstract void onError(Error error);

    public abstract Type getClassType();
}
