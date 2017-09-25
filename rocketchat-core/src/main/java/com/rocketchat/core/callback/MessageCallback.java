package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.Message;

import java.lang.reflect.Type;

/**
 * Created by sachin on 22/7/17.
 */

public class MessageCallback {
    public interface SubscriptionCallback extends Listener {
        void onMessage(String roomId, Message message);
    }

    public static abstract class MessageAckCallback extends Callback {
        public abstract void onMessageAck(Message message);

        @Override
        public Type getClassType() {
            return MessageAckCallback.class;
        }
    }
}
