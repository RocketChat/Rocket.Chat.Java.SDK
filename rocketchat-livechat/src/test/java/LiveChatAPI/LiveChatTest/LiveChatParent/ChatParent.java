package LiveChatAPI.LiveChatTest.LiveChatParent;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import com.rocketchat.livechat.LiveChatAPI;

/**
 * Created by sachin on 17/7/17.
 */
public class ChatParent {
    private static String serverurl = "wss://livechattest.rocket.chat/websocket";

    public LiveChatAPI api;

    @Before
    public void setUpBefore() {
        MockitoAnnotations.initMocks(this);
        System.out.println("before got called");
        api = new LiveChatAPI(serverurl);
        api.setReconnectionStrategy(null);
    }
}
