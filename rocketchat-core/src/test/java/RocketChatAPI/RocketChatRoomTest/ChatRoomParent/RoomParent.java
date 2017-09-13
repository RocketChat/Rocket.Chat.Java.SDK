package RocketChatAPI.RocketChatRoomTest.ChatRoomParent;

import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by sachin on 2/8/17.
 */
public class RoomParent {

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
    }

    @After
    public void logout() throws InterruptedException {
        api.logout();
        api.disconnect();
    }

    public CompletableFuture<TokenObject> login() {
        return api.singleConnect().thenCompose(v -> api.login(username, password));
    }

    protected CompletableFuture<RocketChatAPI.ChatRoom> getChatRoom() {
        return getSubscriptions()
                .thenApply(subscriptions -> {
                    ChatRoomFactory factory = api.getChatRoomFactory();
                    //Listing number of rooms
                    for (RocketChatAPI.ChatRoom room : factory.getChatRooms()) {
                        System.out.println("Room name is " + room.getRoomData().getRoomName());
                        System.out.println("Room type is " + room.getRoomData().getRoomType());
                        System.out.println("Room id is " + room.getRoomData().getRoomId());
                    }
                    return factory.createChatRooms(subscriptions).getChatRoomByName("PUBLICTESTGROUP");
                });
    }

    protected CompletableFuture<List<SubscriptionObject>> getSubscriptions() {
        return login().thenCompose(tokenObject -> api.getSubscriptions());
    }
}

