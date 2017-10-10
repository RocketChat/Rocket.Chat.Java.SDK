package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.livechat.callback.MessageListener;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatMessage;
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
public class CloseConversationTest extends RoomParent {

    @Mock
    MessageListener.SubscriptionListener subscriptionListener;

    @Captor
    ArgumentCaptor<String> roomIdCaptor;

    @Captor
    ArgumentCaptor<LiveChatMessage> messageObjectArgumentCaptor;

    @Before
    public void setup() {
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
        room.sendMessage("Hello there, I'm closing the room");
        room.subscribeRoom(null, subscriptionListener);
        room.closeConversation();
    }

    @Test
    public void closeConversationTest() {
        Mockito.verify(subscriptionListener, timeout(8000).atLeastOnce()).onAgentDisconnect(roomIdCaptor.capture(), messageObjectArgumentCaptor.capture());
        Assert.assertNotNull(roomIdCaptor.getValue());
        Assert.assertNotNull(messageObjectArgumentCaptor.getValue());
        System.out.println("Room closed with Id " + roomIdCaptor.getValue() + " by " + messageObjectArgumentCaptor.getValue().getSender().getUserName());
    }
}
