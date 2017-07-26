package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.model.Permission;
import io.rocketchat.core.model.PublicSetting;

import java.util.ArrayList;

/**
 * Created by sachin on 26/7/17.
 */
public class AccountListener {
    public interface getPermissionsListener {
        void onGetPermissions(ArrayList <Permission> permissions, ErrorObject error);
    }
    public interface getPublicSettingsListener {
        void onGetPublicSettings(ArrayList <PublicSetting> settings, ErrorObject error);
    }
}
