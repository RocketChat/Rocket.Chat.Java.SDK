package io.rocketchat.livechat.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.livechat.model.LiveChatMessage;

import java.util.ArrayList;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Used to get messages available in the history
 */

public interface LoadHistoryListener extends Listener {
    void onLoadHistory(ArrayList<LiveChatMessage> list, int unreadNotLoaded, ErrorObject error);
}
