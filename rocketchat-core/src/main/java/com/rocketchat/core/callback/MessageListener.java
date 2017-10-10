package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RocketChatMessage;
import java.util.List;

/**
 * Created by sachin on 22/7/17.
 */

public class MessageListener {
    public interface SubscriptionListener extends Listener {
        void onMessage(String roomId, RocketChatMessage message);
    }

    public interface MessageAckListener extends Listener {
        void onMessageAck(RocketChatMessage message, ErrorObject error);
    }

}
