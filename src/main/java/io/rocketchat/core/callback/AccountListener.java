package io.rocketchat.core.callback;

import java.util.List;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.Permission;
import io.rocketchat.core.model.PublicSetting;

/**
 * Created by sachin on 26/7/17.
 */
public class AccountListener {
    public interface getPermissionsListener extends Listener {
        void onGetPermissions(List<Permission> permissions, ErrorObject error);
    }

    public interface getPublicSettingsListener extends Listener {
        void onGetPublicSettings(List<PublicSetting> settings, ErrorObject error);
    }
}
