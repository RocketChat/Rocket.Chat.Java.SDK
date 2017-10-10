package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.livechat.callback.MessageListener;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatMessage;
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
public class SendMessageTest extends RoomParent {

    @Mock
    MessageListener.MessageAckListener messageAckListener;

    @Captor
    ArgumentCaptor<LiveChatMessage> messageObjectArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorObjectArgumentCaptor;

    @Before
    public void setup() {
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
        room.sendMessage("Hello there, how are you?", messageAckListener);
    }

    @Test
    public void testSendMessage() {
        Mockito.verify(messageAckListener, timeout(8000).atLeastOnce()).onMessageAck(messageObjectArgumentCaptor.capture(), errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue() == null);
        Assert.assertNotNull(messageObjectArgumentCaptor.getValue());
        System.out.println("Message sent is " + messageObjectArgumentCaptor.getValue().getMessage());
    }

    @After
    public void closeTest() {
        System.out.println("Closing the conversation");
        closeConversation();
    }
}
