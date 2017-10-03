package com.rocketchat.livechat.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.livechat.model.GuestObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Gets called during user registration or login. Returns agent info. having visitorToken and userId
 */

public class AuthCallback {
    public interface RegisterCallback extends Callback {
        void onRegister(GuestObject object);
    }

    public interface LoginCallback extends Callback {
        void onLogin(GuestObject object);
    }
}
