package io.rocketchat.livechat.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.model.MessageObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 *  Used to get message, which is returned after SubType to particular room
 */

public class MessageListener  {
    public interface SubscriptionListener extends Listener{
        void onMessage(String roomId, MessageObject object);
        void onAgentDisconnect(String roomId, MessageObject object);
    }
    public interface MessageAckListener extends Listener{
        void onMessageAck(MessageObject object, ErrorObject error);
    }
}
