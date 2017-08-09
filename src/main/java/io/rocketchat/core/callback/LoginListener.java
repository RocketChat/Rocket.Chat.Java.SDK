package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.TokenObject;

/**
 * Created by sachin on 18/7/17.
 */
public interface LoginListener extends Listener {
    void onLogin(TokenObject token, ErrorObject error);
}
