package io.rocketchat.livechat.callbacks;

/**
 * Created by sachin on 12/6/17.
 */

/**
 * Gets called after successful connection with server
 */

public interface ConnectCallback extends Callback{
    void onConnect(String sessionID);
}
