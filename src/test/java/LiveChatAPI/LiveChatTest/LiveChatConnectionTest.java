package LiveChatAPI.LiveChatTest;

import LiveChatAPI.LiveChatTest.LiveChatParent.ChatParent;
import io.rocketchat.livechat.LiveChatAPI;
import io.rocketchat.livechat.callback.ConnectListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.timeout;


/**
 * Created by sachin on 15/7/17.
 */

public class LiveChatConnectionTest extends ChatParent{

    @Mock
    ConnectListener listener;

    @Captor
    ArgumentCaptor <String> connectCaptor;

    @Captor
    ArgumentCaptor <Boolean> disconnectCaptor;

    @Captor
    ArgumentCaptor <Exception> connectErrorCaptor;

    @Before
    public void setup(){
        setUpBefore();
    }

    @Test
    public void connectTest(){
        api.connect(listener);
        Mockito.verify(listener, timeout(5000).atLeastOnce()).onConnect(connectCaptor.capture());
        Assert.assertTrue(connectCaptor.getValue() != null);
        System.out.println("Value is " + connectCaptor.getValue());
    }

    @Test
    public void disconnectTest(){

    }

    @Test
    public void connectErrorTest(){

    }
}
