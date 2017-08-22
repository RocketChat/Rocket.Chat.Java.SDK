package LiveChatAPI.LiveChatTest;

import LiveChatAPI.LiveChatTest.LiveChatParent.ChatParent;
import com.rocketchat.common.listener.ConnectListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 15/7/17.
 */

public class ConnectionTest extends ChatParent {

    @Mock
    ConnectListener listener;

    @Captor
    ArgumentCaptor<String> connectCaptor;

    @Captor
    ArgumentCaptor<Boolean> disconnectCaptor;

    @Captor
    ArgumentCaptor<Exception> connectErrorCaptor;

    @Test
    public void connectTest() {
        api.connect(listener);
        Mockito.verify(listener, timeout(5000).atLeastOnce()).onConnect(connectCaptor.capture());
        Assert.assertTrue(connectCaptor.getValue() != null);
        System.out.println("Value is " + connectCaptor.getValue());
    }

    @Test
    public void disconnectTest() {

    }

    @Test
    public void connectErrorTest() {

    }
}
