package RocketChatAPI.RocketChatTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.callback.RoomListener;
import io.rocketchat.core.model.TokenObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class CreatePublicGroupTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    String groupName = "PUBLICTESTGROUP";

    @Mock
    RoomListener.GroupListener listener;

    @Captor
    ArgumentCaptor<String> roomIdArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    @Before
    public void setUp() {
        super.setUpBefore(true);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected successfully");
        api.login(username, password, this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.createPublicGroup(groupName, new String[]{}, false, listener);
    }

    @Test
    public void createPublicGroupTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onCreateGroup(roomIdArgumentCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(roomIdArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        System.out.println("Room id is " + roomIdArgumentCaptor.getValue());
    }

    @After
    public void logout() {
        api.logout(null);
    }
}
