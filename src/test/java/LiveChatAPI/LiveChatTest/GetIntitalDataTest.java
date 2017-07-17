package LiveChatAPI.LiveChatTest;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.ConnectListener;
import io.rocketchat.livechat.callback.InitialDataListener;
import io.rocketchat.livechat.model.LiveChatConfigObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */
public class GetIntitalDataTest implements ConnectListener {

    private static String serverurl="wss://livechattest.rocket.chat/websocket";

    LiveChatAPI api;

    @Mock
    InitialDataListener dataListener;

    @Captor
    ArgumentCaptor <LiveChatConfigObject> configObject;

    @Captor
    ArgumentCaptor <ErrorObject> error;

    @Before
    public void setUpBefore(){
        MockitoAnnotations.initMocks( this );
        System.out.println("before got called");
        api= new LiveChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    @Test
    public void testInitialData(){
        Mockito.verify(dataListener, timeout(6000).atLeastOnce()).onInitialData(configObject.capture(),error.capture());
        Assert.assertTrue(error.getValue() == null);
        Assert.assertNotNull(configObject);
        System.out.println("Configuration Object is " + configObject.getValue());
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.getInitialData(dataListener);
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Connect error to server");
    }
}
