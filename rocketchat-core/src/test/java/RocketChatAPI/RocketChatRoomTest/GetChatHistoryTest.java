package RocketChatAPI.RocketChatRoomTest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.core.callback.HistoryListener;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.SubscriptionObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class GetChatHistoryTest extends RoomParent {

    @Mock
    HistoryListener listener;

    @Captor
    ArgumentCaptor<ArrayList<RocketChatMessage>> listArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> unreadNotLoadedCaptor;
    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        super.onGetSubscriptions(subscriptions, error);
        room.getChatHistory(10, new Date(), null, listener);
    }

    @Test
    public void getChatHistoryTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onLoadHistory(listArgumentCaptor.capture(), unreadNotLoadedCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(listArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        Assert.assertTrue(listArgumentCaptor.getValue().size() > 0);

        for (RocketChatMessage message : listArgumentCaptor.getValue()) {
            System.out.println("Message is " + message.getMessage());
        }
    }

}
