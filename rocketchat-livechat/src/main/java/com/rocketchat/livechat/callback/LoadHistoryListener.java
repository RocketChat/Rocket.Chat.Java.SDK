package com.rocketchat.livechat.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.LiveChatMessage;
import java.util.List;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Used to get messages available in the history
 */

public interface LoadHistoryListener extends Listener {
    void onLoadHistory(List<LiveChatMessage> list, int unreadNotLoaded, ErrorObject error);
}
