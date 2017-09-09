package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.TokenObject;

/**
 * Created by sachin on 18/7/17.
 */
public interface LoginCallback extends Callback {
    void onLoginSuccess(TokenObject token);
}
