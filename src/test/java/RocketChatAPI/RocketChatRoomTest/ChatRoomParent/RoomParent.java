package RocketChatAPI.RocketChatRoomTest.ChatRoomParent;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.model.TokenObject;
import org.junit.After;
import org.mockito.MockitoAnnotations;

/**
 * Created by sachin on 2/8/17.
 */
public class RoomParent extends CoreAdapter{

    private static String serverurl="wss://demo.rocket.chat/websocket";

    String username="testuserrocks";
    String password="testuserrocks";

    public RocketChatAPI api;

    public void setUpBefore(Boolean connect){
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

    @After
    public void logout(){
        api.logout(null);
    }
}

