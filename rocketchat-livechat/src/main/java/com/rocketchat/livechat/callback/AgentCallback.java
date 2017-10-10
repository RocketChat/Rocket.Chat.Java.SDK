package com.rocketchat.livechat.callback;

import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.AgentObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Getting agent info. from the server
 */
public class AgentCallback {
    public interface AgentDataCallback extends Callback {
        void onAgentData(AgentObject agentObject);
    }

    public interface AgentConnectListener extends Listener {
        void onAgentConnect(AgentObject agentObject);
    }
}
