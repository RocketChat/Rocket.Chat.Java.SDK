package io.rocketchat.livechat.callbacks;

import io.rocketchat.livechat.models.MessageObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 *  Used to get message, which is returned after subscription to particular room
 */

public interface MessageCallback extends Callback {
    void call(String roomId,MessageObject object);
}
