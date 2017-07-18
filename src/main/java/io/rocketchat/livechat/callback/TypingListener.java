package io.rocketchat.livechat.callback;

/**
 * Created by sachin on 10/6/17.
 */

import io.rocketchat.common.listener.Listener;

/**
 * Getting event about room-user whether he is typing or not
 */

public interface TypingListener extends Listener{
    void onTyping(String roomId, String user, Boolean istyping);
}
