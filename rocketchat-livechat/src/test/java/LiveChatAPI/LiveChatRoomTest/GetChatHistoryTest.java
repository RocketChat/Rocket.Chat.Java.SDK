package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.livechat.callback.LoadHistoryListener;
import com.rocketchat.livechat.model.GuestObject;
import com.rocketchat.livechat.model.LiveChatMessage;
import java.util.ArrayList;
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

public class GetChatHistoryTest extends RoomParent {

    @Mock
    LoadHistoryListener loadHistoryListener;

    @Captor
    ArgumentCaptor<ArrayList<LiveChatMessage>> listArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> unreadNotLoadedCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorObjectArgumentCaptor;

    @Before
    public void setup() {
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
        room.sendMessage("Hey there");
        room.sendMessage("Whats up with this thing");
        room.sendMessage("I know you are there");

        room.getChatHistory(20, null, null, loadHistoryListener);
    }

    @Test
    public void getMessageHistoryTest() {
        Mockito.verify(loadHistoryListener, timeout(8000).atLeastOnce()).onLoadHistory(listArgumentCaptor.capture(), unreadNotLoadedCaptor.capture(), errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue() == null);
        Assert.assertNotNull(unreadNotLoadedCaptor.getValue());
        Assert.assertTrue(listArgumentCaptor.getValue().size() > 2);
        System.out.println("History is " + listArgumentCaptor.getValue());

    }

    @After
    public void closeTest() {
        System.out.println("Closing the conversation");
        closeConversation();
    }
}
