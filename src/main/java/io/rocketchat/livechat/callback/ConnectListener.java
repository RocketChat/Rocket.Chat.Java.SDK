package io.rocketchat.livechat.callback;

/**
 * Created by sachin on 12/6/17.
 */


import io.rocketchat.common.listener.Listener;

/**
 * Gets called after successful connection with server
 */

public interface ConnectListener extends Listener {
    void onConnect(String sessionID);
    void onDisconnect(boolean closedByServer);
    void onConnectError(Exception websocketException);
}
