package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RocketChatMessage;
import java.util.List;

/**
 * Created by sachin on 22/7/17.
 */

public class MessageCallback {
    public interface SubscriptionCallback extends Listener {
        void onMessage(String roomId, RocketChatMessage message);
    }

    public interface MessageAckCallback extends Callback {
        void onMessageAck(RocketChatMessage message);
    }
}
