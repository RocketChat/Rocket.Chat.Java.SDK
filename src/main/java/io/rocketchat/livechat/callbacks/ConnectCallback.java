package io.rocketchat.livechat.callbacks;

/**
 * Created by sachin on 12/6/17.
 */

public interface ConnectCallback extends Callback{
    void onConnect(String sessionID);
}
