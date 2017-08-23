package RocketChatAPI.RocketChatRoomTest.ChatRoomParent;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.adapter.CoreAdapter;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by sachin on 2/8/17.
 */
public class RoomParent extends CoreAdapter {

    private static String serverurl = "wss://demo.rocket.chat/websocket";
    public RocketChatAPI api;
    public RocketChatAPI.ChatRoom room;
    String username = "testuserrocks";
    String password = "testuserrocks";

    @Before
    public void setUpBefore() {
        MockitoAnnotations.initMocks(this);
        System.out.println("before got called");
        api = new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    @Override
    public void onConnect(String sessionID) {
        api.login(username, password, RoomParent.this);

    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);

    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        ChatRoomFactory factory = api.getChatRoomFactory();
        //Listing number of rooms
        for (RocketChatAPI.ChatRoom room : factory.getChatRooms()) {
            System.out.println("Room name is " + room.getRoomData().getRoomName());
            System.out.println("Room type is " + room.getRoomData().getRoomType());
            System.out.println("Room id is " + room.getRoomData().getRoomId());
        }
        room = factory.createChatRooms(subscriptions).getChatRoomByName("PUBLICTESTGROUP");
    }

    @After
    public void logout() throws InterruptedException {
        api.logout(null);
        api.disconnect();
    }
}

