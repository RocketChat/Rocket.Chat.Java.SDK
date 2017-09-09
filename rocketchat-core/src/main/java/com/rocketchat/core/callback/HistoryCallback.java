package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.RocketChatMessage;

import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public interface HistoryCallback extends Callback {
    void onLoadHistory(List<RocketChatMessage> list, int unreadNotLoaded);
}
