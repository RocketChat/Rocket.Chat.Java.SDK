package io.rocketchat.livechat.callback;

import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.MessageObject;

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
    public interface AgentConnectListener extends Listener{
        void onAgentConnect(AgentObject agentObject);
    }
    public interface AgentDisconnectListener extends Listener{
        void onAgentDisconnect(String roomId, MessageObject object);
    }
}
