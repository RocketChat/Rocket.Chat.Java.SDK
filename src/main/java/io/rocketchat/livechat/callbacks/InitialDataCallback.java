package io.rocketchat.livechat.callbacks;
import io.rocketchat.livechat.models.LiveChatConfigObject;

/**
 * Created by sachin on 9/6/17.
 */

public interface InitialDataCallback extends Callback{
    void call(LiveChatConfigObject object);
}
