package io.rocketchat.livechat.callbacks;

import io.rocketchat.livechat.models.AgentObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Getting agent info. from the server
 */
public interface AgentCallback extends Callback {
    void call(AgentObject object);
}
