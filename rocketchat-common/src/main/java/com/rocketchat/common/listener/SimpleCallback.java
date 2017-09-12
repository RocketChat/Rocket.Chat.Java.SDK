package com.rocketchat.common.listener;

import java.lang.reflect.Type;

/**
 * Created by sachin on 26/7/17.
 */
public abstract class SimpleCallback extends Callback {
    public abstract void onSuccess();

    @Override
    public Type getClassType() {
        return SimpleCallback.class;
    }
}
