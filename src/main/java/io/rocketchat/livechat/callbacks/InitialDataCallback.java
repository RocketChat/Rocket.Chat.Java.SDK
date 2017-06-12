package io.rocketchat.livechat.callbacks;
import io.rocketchat.livechat.models.LiveChatConfigObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * gets called after getInitialData event, return contains configuration object
 */
public interface InitialDataCallback extends Callback{
    void call(LiveChatConfigObject object);
}
