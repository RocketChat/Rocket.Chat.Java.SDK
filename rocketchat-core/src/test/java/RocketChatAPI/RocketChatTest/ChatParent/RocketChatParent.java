package RocketChatAPI.RocketChatTest.ChatParent;

import com.rocketchat.core.RocketChatAPI;
import org.mockito.MockitoAnnotations;

/**
 * Created by sachin on 2/8/17.
 */
public class RocketChatParent {

    private static String serverurl = "wss://demo.rocket.chat/websocket";

    public RocketChatAPI api;

    public void setUpBefore() {
        MockitoAnnotations.initMocks(this);
        System.out.println("before got called");
        api = new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
    }
}
