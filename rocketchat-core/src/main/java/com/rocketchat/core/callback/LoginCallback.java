package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.TokenObject;

import java.lang.reflect.Type;

/**
 * Created by sachin on 18/7/17.
 */
public abstract class LoginCallback extends Callback {
    public abstract void onLoginSuccess(TokenObject token);

    @Override
    public Type getClassType() {
        return LoginCallback.class;
    }
}
