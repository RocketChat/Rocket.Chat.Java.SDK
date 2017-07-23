package io.rocketchat.core.callback;

import io.rocketchat.core.middleware.CoreStreamMiddleware;

/**
 * Created by sachin on 23/7/17.
 */
public interface SubscribeListener {

    /**
     *
     * @param type Subscription type : stream-room-messages, stream-livechat-room, typing stream
     * @param subId It represents uniqueID used for SubType
     */

    void onSubscribe(CoreStreamMiddleware.SubType type, String subId);
}
