package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.Message;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.RocketChatMessage;

import java.util.ArrayList;

/**
 * Created by sachin on 21/7/17.
 */
public interface HistoryListener extends Listener{
    void onLoadHistory(ArrayList<RocketChatMessage> list, int unreadNotLoaded, ErrorObject error);
}
