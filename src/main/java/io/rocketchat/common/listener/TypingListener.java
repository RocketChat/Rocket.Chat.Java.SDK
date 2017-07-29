package io.rocketchat.common.listener;

/**
 * Created by sachin on 10/6/17.
 */

/**
 * Getting event about room-user whether he is typing or not
 */

public interface TypingListener extends Listener{
    void onTyping(String roomId, String user, Boolean istyping);
}
