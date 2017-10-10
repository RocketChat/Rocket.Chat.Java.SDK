package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RocketChatMessage;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public interface HistoryListener extends Listener {
    void onLoadHistory(List<RocketChatMessage> list, int unreadNotLoaded, ErrorObject error);
}
