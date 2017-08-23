package com.rocketchat.common.listener;

/**
 * Created by sachin on 12/6/17.
 */

/**
 * Gets called after successful connection with server
 */

public interface ConnectListener extends Listener {
    void onConnect(String sessionID);

    void onDisconnect(boolean closedByServer);

    void onConnectError(Throwable websocketException);
}
