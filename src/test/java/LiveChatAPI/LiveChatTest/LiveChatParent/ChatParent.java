package LiveChatAPI.LiveChatTest.LiveChatParent;

import io.rocketchat.livechat.LiveChatAPI;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by sachin on 17/7/17.
 */
public class ChatParent {
    private static String serverurl="wss://livechattest.rocket.chat/websocket";

    public LiveChatAPI api;

    @Before
    public void setUpBefore(){
        MockitoAnnotations.initMocks( this );
        System.out.println("before got called");
        api= new LiveChatAPI(serverurl);
        api.setReconnectionStrategy(null);
    }
}
