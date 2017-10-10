package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.core.model.Message;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sachin on 21/7/17.
 */
public interface HistoryCallback extends Callback {
    void onLoadHistory(List<Message> list, int unreadNotLoaded);
}
