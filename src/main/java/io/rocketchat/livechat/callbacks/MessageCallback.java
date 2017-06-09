package io.rocketchat.livechat.callbacks;

import io.rocketchat.livechat.models.MessageObject;

/**
 * Created by sachin on 9/6/17.
 */
public interface MessageCallback extends Callback {
    void call(String roomId,MessageObject object);
}
