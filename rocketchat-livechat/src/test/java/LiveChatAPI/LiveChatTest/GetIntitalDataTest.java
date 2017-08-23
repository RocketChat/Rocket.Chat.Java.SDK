package LiveChatAPI.LiveChatTest;

import LiveChatAPI.LiveChatTest.LiveChatParent.ChatParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.livechat.callback.InitialDataListener;
import com.rocketchat.livechat.model.LiveChatConfigObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 17/7/17.
 */
public class GetIntitalDataTest extends ChatParent implements ConnectListener {

    @Mock
    InitialDataListener dataListener;

    @Captor
    ArgumentCaptor<LiveChatConfigObject> configObject;

    @Captor
    ArgumentCaptor<ErrorObject> error;


    @Override
    public void setUpBefore() {
        super.setUpBefore();
        api.connect(this);
    }

    @Test
    public void testInitialData() {
        Mockito.verify(dataListener, timeout(6000).atLeastOnce()).onInitialData(configObject.capture(), error.capture());
        Assert.assertTrue(error.getValue() == null);
        Assert.assertNotNull(configObject.getValue());
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
