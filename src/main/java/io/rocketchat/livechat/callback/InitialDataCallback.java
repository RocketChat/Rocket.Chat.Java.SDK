package io.rocketchat.livechat.callback;
import io.rocketchat.livechat.model.LiveChatConfigObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * gets called after getInitialData event, return contains configuration object
 */
public interface InitialDataCallback extends Callback{
    void call(LiveChatConfigObject object);
}
