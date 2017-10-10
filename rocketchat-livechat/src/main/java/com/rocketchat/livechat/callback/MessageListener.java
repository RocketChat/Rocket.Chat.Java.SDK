package com.rocketchat.livechat.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.LiveChatMessage;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Used to get message, which is returned after SubType to particular room
 */

public class MessageListener {
    public interface SubscriptionListener extends Listener {
        void onMessage(String roomId, LiveChatMessage object);

        void onAgentDisconnect(String roomId, LiveChatMessage object);
    }

    public interface MessageAckCallback extends Callback {
        void onMessageAck(LiveChatMessage object);
    }

    public interface OfflineMessageCallback extends Callback {
        void onOfflineMesssageSuccess(Boolean success);
    }
}
