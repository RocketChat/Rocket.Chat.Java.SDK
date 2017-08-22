package RocketChatAPI.RocketChatRoomTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.core.model.SubscriptionObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class SubscribeTest extends RoomParent {

    @Rule
    public TestName testName = new TestName();
    @Mock
    SubscribeListener listener;
    @Captor
    ArgumentCaptor<Boolean> isSubscribed;
    @Captor
    ArgumentCaptor<String> stringSubArgumentCaptor;

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        super.onGetSubscriptions(subscriptions, error);
        if (testName.getMethodName().equals("subscribeRoomMessageEventTest")) {
            room.subscribeRoomMessageEvent(listener, null);
        } else if (testName.getMethodName().equals("subsribeRoomTypingEventTest")) {
            room.subscribeRoomTypingEvent(listener, null);
        }
    }

    @Test
    public void subscribeRoomMessageEventTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onSubscribe(isSubscribed.capture(), stringSubArgumentCaptor.capture());
        Assert.assertNotNull(isSubscribed.getValue());
        Assert.assertNotNull(stringSubArgumentCaptor.getValue());
    }

    @Test
    public void subsribeRoomTypingEventTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onSubscribe(isSubscribed.capture(), stringSubArgumentCaptor.capture());
        Assert.assertNotNull(isSubscribed.getValue());
        Assert.assertNotNull(stringSubArgumentCaptor.getValue());
    }
}
