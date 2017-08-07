package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SubscribeListener;
import io.rocketchat.core.model.SubscriptionObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class SubscribeTest extends RoomParent{

    @Mock
    SubscribeListener listener;

    @Captor
    ArgumentCaptor <Boolean> isSubscribed;

    @Captor
    ArgumentCaptor <String> stringSubArgumentCaptor;

    @Rule
    public TestName testName = new TestName();

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error) {
        super.onGetSubscriptions(subscriptions, error);
        if (testName.getMethodName().equals("subscribeRoomMessageEventTest")) {
            room.subscribeRoomMessageEvent(listener, null);
        }else if (testName.getMethodName().equals("subsribeRoomTypingEventTest")){
            room.subscribeRoomTypingEvent(listener,null);
        }
    }

    @Test
    public void subscribeRoomMessageEventTest(){
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onSubscribe(isSubscribed.capture(),stringSubArgumentCaptor.capture());
        Assert.assertNotNull(isSubscribed.getValue());
        Assert.assertNotNull(stringSubArgumentCaptor.getValue());
    }

    @Test
    public void subsribeRoomTypingEventTest(){
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onSubscribe(isSubscribed.capture(),stringSubArgumentCaptor.capture());
        Assert.assertNotNull(isSubscribed.getValue());
        Assert.assertNotNull(stringSubArgumentCaptor.getValue());
    }
}
