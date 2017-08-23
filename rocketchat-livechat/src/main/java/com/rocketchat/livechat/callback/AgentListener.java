package com.rocketchat.livechat.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.livechat.model.AgentObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Getting agent info. from the server
 */
public class AgentListener {
    public interface AgentDataListener extends Listener {
        void onAgentData(AgentObject agentObject, ErrorObject error);
    }

    public interface AgentConnectListener extends Listener {
        void onAgentConnect(AgentObject agentObject);
    }
}
