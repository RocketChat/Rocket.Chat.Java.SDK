package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.SimpleListener;
import com.rocketchat.core.callback.MessageListener;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.SubscriptionObject;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageOpTest extends RoomParent {

    @Rule
    public TestName testName = new TestName();

    @Mock
    SimpleListener listener;

    @Captor
    ArgumentCaptor<Boolean> successCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    String msgId;

    MessageListener.MessageAckListener acklistener = new MessageListener.MessageAckListener() {
        @Override
        public void onMessageAck(RocketChatMessage message, ErrorObject error) {
            msgId = message.getMessageId();
            if (testName.getMethodName().equals("A_updateMessageTest")) {
                room.updateMessage(message.getMessageId(), "This is a updated message", listener);
            } else if (testName.getMethodName().equals("B_pinMessageTest")) {
                room.pinMessage(message.getRawJsonObject(), listener);
            } else if (testName.getMethodName().equals("C_unpinMessageTest")) {
                room.unpinMessage(message.getRawJsonObject(), listener);
            } else if (testName.getMethodName().equals("D_starMessageTest")) {
                room.starMessage(msgId, true, listener);
            } else if (testName.getMethodName().equals("E_deleteMessageTest")) {
                room.deleteMessage(msgId, listener);
            }
        }
    };

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        super.onGetSubscriptions(subscriptions, error);
        room.sendMessage("Hey there bro", acklistener);
    }

    public void TestThisCode() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).callback(successCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(successCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
    }

    @Test
    public void A_updateMessageTest() {
        TestThisCode();
    }

    @Test
    public void B_pinMessageTest() {
        TestThisCode();
    }

    @Test
    public void C_unpinMessageTest() {
        TestThisCode();
    }

    @Test
    public void D_starMessageTest() {
        TestThisCode();
    }

    @Test
    public void E_deleteMessageTest() {
        TestThisCode();
    }

    @Override
    public void logout() throws InterruptedException {
        if (msgId != null) {
            room.deleteMessage(msgId, null);
        }
        super.logout();
    }
}
