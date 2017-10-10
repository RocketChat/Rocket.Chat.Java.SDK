package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.Token;

import java.lang.reflect.Type;

/**
 * Created by sachin on 18/7/17.
 */
public interface LoginCallback extends Callback {
    /**
     * Called when the Login was successful. The callback may proceed to read the {@link Token}
     */
    void onLoginSuccess(Token token);
}
