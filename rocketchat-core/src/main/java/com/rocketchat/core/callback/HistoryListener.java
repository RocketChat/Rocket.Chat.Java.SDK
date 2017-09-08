package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RocketChatMessage;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public interface HistoryListener extends Listener {
    void onLoadHistory(List<RocketChatMessage> list, int unreadNotLoaded, ApiError error);
}
