package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ServerInfo;
import com.rocketchat.common.listener.Callback;

public interface ServerInfoCallback extends Callback {
    void onServerInfo(ServerInfo info);
}
