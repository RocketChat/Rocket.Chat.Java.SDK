package io.rocketchat.livechat.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.livechat.model.LiveChatConfigObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * gets called after getInitialData event, return contains configuration object
 */
public interface InitialDataListener extends Listener {
    void onInitialData(LiveChatConfigObject object, ErrorObject error);
}
