package com.rocketchat.core.callback;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.data.model.User;
import com.rocketchat.common.listener.Listener;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public class UserListener {
    public interface getUserRoleListener extends Listener {
        void onUserRoles(List<User> users, RocketChatApiException error);
    }
}
