package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SubscribeListener;
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

public class SubscribeTest extends RoomParent {

    @Mock
    SubscribeListener listener;

    @Captor
    ArgumentCaptor <Boolean> typeArgumentCaptor;

    @Captor
    ArgumentCaptor <String> subscriptionId;

    @Before
    public void setup(){
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
        room.subscribeLiveChatRoom(listener,null);
        room.sendMessage("Hey there, yo yo");
        room.subscribeRoom(listener,null);
        room.subscribeTyping(listener,null);
    }

    @Test
    public void subscriptionTest(){
        Mockito.verify(listener, timeout(8000).times(3)).onSubscribe(typeArgumentCaptor.capture(),subscriptionId.capture());
        Assert.assertNotNull(typeArgumentCaptor.getValue());
        Assert.assertNotNull(subscriptionId.getValue());
        System.out.println("This method is gonna called 3 times");
    }

    @After
    public void closeTest(){
        System.out.println("Closing the conversation");
        closeConversation();
    }
}
