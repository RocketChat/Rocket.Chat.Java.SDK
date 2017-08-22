package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.Listener;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public class UserListener {
    public interface getUserRoleListener extends Listener {
        void onUserRoles(List<UserObject> users, ErrorObject error);
    }
}
