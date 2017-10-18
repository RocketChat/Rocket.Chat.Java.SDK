package com.rocketchat.common.listener;

import com.rocketchat.common.data.lightstream.document.UserDocument;

public interface ActiveUsersListener extends Listener {
    void onUserAdded(UserDocument user);

    void onUserUpdated(UserDocument user);

    void onUserRemoved(String id);
}
