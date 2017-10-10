package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.listener.SubscribeListener;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

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

    @Test(timeout = 12000)
    public void subscribeRoomMessageEventTest() throws Exception {
        getChatRoom().get().subscribeRoomMessageEvent(listener, null);
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onSubscribe(isSubscribed.capture(), stringSubArgumentCaptor.capture());
        Assert.assertNotNull(isSubscribed.getValue());
        Assert.assertNotNull(stringSubArgumentCaptor.getValue());
    }

    @Test(timeout = 12000)
    public void subsribeRoomTypingEventTest() throws Exception {
        getChatRoom().get().subscribeRoomTypingEvent(listener, null);
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onSubscribe(isSubscribed.capture(), stringSubArgumentCaptor.capture());
        Assert.assertNotNull(isSubscribed.getValue());
        Assert.assertNotNull(stringSubArgumentCaptor.getValue());
    }
}
