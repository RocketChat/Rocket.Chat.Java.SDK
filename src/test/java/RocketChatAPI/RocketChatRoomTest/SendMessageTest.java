package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.callback.MessageListener;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.SubscriptionObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class SendMessageTest extends RoomParent{

    @Mock
    MessageListener.MessageAckListener listener;


    @Captor
    ArgumentCaptor <RocketChatMessage> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {
        super.onGetSubscriptions(subscriptions, error);
        room.sendMessage("Hey there how are you",listener);
    }

    @Test
    public void sendMessageTest(){
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onMessageAck(messageArgumentCaptor.capture(),errorArgumentCaptor.capture());
        Assert.assertNotNull(messageArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        System.out.println("Message is "+messageArgumentCaptor.getValue().getMessage());
    }
}
