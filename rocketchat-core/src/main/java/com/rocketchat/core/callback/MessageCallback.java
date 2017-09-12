package com.rocketchat.core.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RocketChatMessage;

import java.lang.reflect.Type;

/**
 * Created by sachin on 22/7/17.
 */

public class MessageCallback {
    public interface SubscriptionCallback extends Listener {
        void onMessage(String roomId, RocketChatMessage message);
    }

    public static abstract class MessageAckCallback extends Callback {
        public abstract void onMessageAck(RocketChatMessage message);

        @Override
        public Type getClassType() {
            return MessageAckCallback.class;
        }
    }
}
