package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.listener.Listener;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public class UserListener {
    public interface getUserRoleListener extends Listener {
        void onUserRoles(List<UserObject> users, ErrorObject error);
    }
}
