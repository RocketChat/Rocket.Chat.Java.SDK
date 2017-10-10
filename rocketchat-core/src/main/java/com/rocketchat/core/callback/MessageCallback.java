package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.Message;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sachin on 22/7/17.
 */

public class MessageCallback {
    public interface SubscriptionListener extends Listener {
        void onMessage(String roomId, Message message);
    }

    public interface MessageAckCallback extends Callback {
        void onMessageAck(Message message);
    }

    public interface SearchMessageCallback extends Callback {
        void onSearchMessage(List<Message> messageList);
    }
}
