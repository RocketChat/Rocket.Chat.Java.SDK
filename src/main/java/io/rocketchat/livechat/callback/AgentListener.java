package io.rocketchat.livechat.callback;

import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.model.AgentObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Getting agent info. from the server
 */
public class AgentListener{
    public interface AgentDataListener extends Listener{
        void onAgentData(AgentObject agentObject);
    }
    public interface ConnectedAgentListener extends Listener{
        void onConnectedAgent(AgentObject agentObject);
    }
}
