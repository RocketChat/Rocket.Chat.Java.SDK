package io.rocketchat.livechat.callbacks;

/**
 * Created by sachin on 10/6/17.
 */
public interface TypingCallback {
    void call(String roomId,String user, Boolean istyping);
}
