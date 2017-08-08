package io.rocketchat.livechat.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.livechat.model.GuestObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Gets called during user registration or login. Returns agent info. having visitorToken and userId
 */

public class AuthListener {
    public interface RegisterListener extends Listener {
        void onRegister(GuestObject object, ErrorObject error);
    }

    public interface LoginListener extends Listener {
        void onLogin(GuestObject object, ErrorObject error);
    }
}
