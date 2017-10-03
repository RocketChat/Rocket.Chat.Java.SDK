package com.rocketchat.livechat.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.LiveChatMessage;
import java.util.List;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Used to get messages available in the history
 */

public interface LoadHistoryCallback extends Callback {
    void onLoadHistory(List<LiveChatMessage> list, int unreadNotLoaded);
}
