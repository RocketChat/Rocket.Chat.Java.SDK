package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.RocketChatMessage;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public abstract class HistoryCallback extends Callback {
    public abstract void onLoadHistory(List<RocketChatMessage> list, int unreadNotLoaded);

    @Override
    public Type getClassType() {
        return HistoryCallback.class;
    }
}
