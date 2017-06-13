package io.rocketchat.livechat.callback;

import io.rocketchat.livechat.middleware.LiveChatStreamMiddleware;

/**
 * Created by sachin on 12/6/17.
 */
public interface SubscribeListener {
    /**
     *
     * @param type Subscription type : stream-room-messages, stream-livechat-room, typing stream
     * @param subId It represents uniqueID used for SubType
     */
    void onSubscribe(LiveChatStreamMiddleware.SubType type, String subId);
}
