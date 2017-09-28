package com.rocketchat.livechat.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.livechat.model.LiveChatConfigObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * gets called after getInitialData event, return contains configuration object
 */
public interface InitialDataCallback extends Callback {
    void onInitialData(LiveChatConfigObject data);
}
