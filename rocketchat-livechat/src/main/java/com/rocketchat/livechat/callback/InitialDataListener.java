package com.rocketchat.livechat.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.LiveChatConfigObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * gets called after getInitialData event, return contains configuration object
 */
public interface InitialDataListener extends Listener {
    void onInitialData(LiveChatConfigObject object, ErrorObject error);
}
