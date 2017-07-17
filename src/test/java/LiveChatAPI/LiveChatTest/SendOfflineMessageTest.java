package LiveChatAPI.LiveChatTest;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.ConnectListener;
import io.rocketchat.livechat.callback.MessageListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */
public class SendOfflineMessageTest implements ConnectListener {
    private static String serverurl="wss://livechattest.rocket.chat/websocket";

    LiveChatAPI api;

    @Mock
    MessageListener.OfflineMessageListener listener;

    @Captor
    ArgumentCaptor <Boolean> isSent;

    @Captor
    ArgumentCaptor <ErrorObject> errorObjectArgumentCaptor;

    @Before
    public void setUpBefore(){
        MockitoAnnotations.initMocks( this );
        System.out.println("before got called");
        api= new LiveChatAPI(serverurl);
        api.setReconnectionStrategy(null);
        api.connect(this);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.sendOfflineMessage("aditya","aditya123@gmail.com","This is a test message",listener);
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnected from server");
    }

    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Connect error to server");
    }

    @Test
    public void sendOfflineTest(){
        Mockito.verify(listener, timeout(6000).atLeastOnce()).onOfflineMesssageSuccess(isSent.capture(),errorObjectArgumentCaptor.capture());
        Assert.assertTrue(errorObjectArgumentCaptor.getValue() == null);
        Assert.assertTrue(isSent.getValue() != null);
        System.out.println("Offline Message sent is " + isSent.getValue());
    }
}
