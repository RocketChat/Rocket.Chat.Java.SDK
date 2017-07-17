package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.callback.AgentListener;
import io.rocketchat.livechat.model.AgentObject;
import io.rocketchat.livechat.model.GuestObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */
public class GetAgentDataTest extends RoomParent {


    @Mock
    AgentListener.AgentConnectListener listener;

    @Captor
    ArgumentCaptor <AgentObject> agentObjectArgumentCaptor;


    @Mock
    AgentListener.AgentDataListener agentDataListener;

    @Captor
    ArgumentCaptor <ErrorObject> errorObjectArgumentCaptor;

    @Before
    public void setup(){
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
        room.subscribeLiveChatRoom(null,listener);
        room.sendMessage("Hey there");
    }

    @Test
    public void getAgentTest(){
        /**
         * First part
         */
        Mockito.verify(listener, timeout(8000).atLeastOnce()).onAgentConnect(agentObjectArgumentCaptor.capture());
        Assert.assertTrue(agentObjectArgumentCaptor.getValue() != null);
        System.out.println("Agent assigned is "+agentObjectArgumentCaptor.getValue());

        /**
         * Second part
         */
        room.getAgentData(agentDataListener);
        Mockito.verify(agentDataListener, timeout(2000).atLeastOnce()).onAgentData(agentObjectArgumentCaptor.capture(),errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue() == null);
        Assert.assertTrue(agentObjectArgumentCaptor.getValue() != null);
        System.out.println("Got agent data "+agentObjectArgumentCaptor.getValue());
    }

    @After
    public void closeTest(){
        System.out.println("Closing the conversation");
        closeConversation();
    }
}
