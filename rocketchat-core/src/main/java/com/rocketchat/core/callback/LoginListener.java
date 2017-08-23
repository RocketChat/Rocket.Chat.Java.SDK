package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.TokenObject;

/**
 * Created by sachin on 18/7/17.
 */
public interface LoginListener extends Listener {
    void onLogin(TokenObject token, ErrorObject error);
}
