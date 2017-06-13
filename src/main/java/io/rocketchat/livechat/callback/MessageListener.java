package io.rocketchat.livechat.callback;

import io.rocketchat.livechat.model.MessageObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 *  Used to get message, which is returned after subscriptiontype to particular room
 */

public interface MessageListener extends Listener {
    void onMessage(String roomId, MessageObject object);
    void onAgentDisconnect(String roomId, MessageObject object);
}
