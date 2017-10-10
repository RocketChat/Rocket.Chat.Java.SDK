package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.core.callback.RoomListener;
import com.rocketchat.core.model.TokenObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class CreatePrivateGroupTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    String groupName = "PRIVATETESTGROUP";

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
        api.createPrivateGroup(groupName, new String[]{}, listener);
    }

    @Test
    public void createPrivateGroupTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onCreateGroup(roomIdArgumentCaptor.capture(), errorArgumentCaptor.capture());
//        Assert.assertNotNull(roomIdArgumentCaptor.getValue());
//        Assert.assertNull(errorArgumentCaptor.getValue());
//        System.out.println("Room id is " + roomIdArgumentCaptor.getValue());
    }

    @After
    public void logout() {
        api.logout(null);
    }
}
