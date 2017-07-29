package io.rocketchat.livechat.callback;

import io.rocketchat.common.listener.Listener;
import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;

/**
 * Created by sachin on 12/6/17.
 */
// TODO: 29/7/17 Future change to make it more generalized, removing type from the method and adding Boolean success and String subId to the code
public interface SubscribeListener extends Listener{
    /**
     *
     * @param type Subscription type : stream-room-messages, stream-livechat-room, typing stream
     * @param subId It represents uniqueID used for SubType
     */
    void onSubscribe(LiveChatStreamMiddleware.SubType type, String subId);
}
