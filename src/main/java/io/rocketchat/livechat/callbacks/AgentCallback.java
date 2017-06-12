package io.rocketchat.livechat.callbacks;

import io.rocketchat.livechat.models.AgentObject;

import javax.management.DescriptorKey;

/**
 * Created by sachin on 9/6/17.
 */

public interface AgentCallback extends Callback {
    void call(AgentObject object);
}
