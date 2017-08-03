package RocketChatAPI.RocketChatRoomTest.ChatRoomParent;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.factory.ChatRoomFactory;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * Created by sachin on 2/8/17.
 */
public class RoomParent extends CoreAdapter{

    private static String serverurl="wss://demo.rocket.chat/websocket";

    String username="testuserrocks";
    String password="testuserrocks";

    public RocketChatAPI api;
    public RocketChatAPI.ChatRoom room;

    @Before
    public void setUpBefore(){
        MockitoAnnotations.initMocks( this );
        System.out.println("before got called");
        api= new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    @Override
    public void onConnect(String sessionID) {
        api.login(username,password,this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);

    }

    @Override
    public void onGetSubscriptions(ArrayList<SubscriptionObject> subscriptions, ErrorObject error){
        ChatRoomFactory factory=api.getFactory();
        //Listing number of rooms
        for (RocketChatAPI.ChatRoom room : factory.getChatRooms()){
            System.out.println("Room name is "+room.getRoomData().getRoomName());
            System.out.println("Room type is "+room.getRoomData().getRoomType());
            System.out.println("Room id is "+room.getRoomData().getRoomId());
        }
        room=factory.createChatRooms(subscriptions).getChatRoomByName("PUBLICTESTGROUP");
    }


    @After
    public void logout(){
        api.logout(null);
    }
}

