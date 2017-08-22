package RocketChatAPI.RocketChatTest.ChatParent;

import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.adapter.CoreAdapter;
import org.mockito.MockitoAnnotations;

/**
 * Created by sachin on 2/8/17.
 */
public class RocketChatParent extends CoreAdapter {

    private static String serverurl = "wss://demo.rocket.chat/websocket";

    public RocketChatAPI api;

    public void setUpBefore(Boolean connect) {
        MockitoAnnotations.initMocks(this);
        System.out.println("before got called");
        api = new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        if (connect) {
            api.connect(this);
        }
    }
}
