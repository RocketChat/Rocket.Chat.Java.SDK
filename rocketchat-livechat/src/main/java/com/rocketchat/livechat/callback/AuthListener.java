package com.rocketchat.livechat.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.GuestObject;

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
