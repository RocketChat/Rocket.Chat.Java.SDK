package io.rocketchat.livechat.callback;

/**
 * Created by sachin on 10/6/17.
 */

/**
 * Getting event about room-user whether he is typing or not
 */

public interface TypingCallback {
    void call(String roomId,String user, Boolean istyping);
}
