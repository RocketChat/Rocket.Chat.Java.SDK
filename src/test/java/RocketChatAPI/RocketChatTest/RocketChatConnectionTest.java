package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import io.rocketchat.common.listener.ConnectListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 2/8/17.
 */
public class RocketChatConnectionTest  extends RocketChatParent {

    @Mock
    ConnectListener listener;

    @Captor
    ArgumentCaptor<String> connectCaptor;

    @Captor
    ArgumentCaptor <Boolean> disconnectCaptor;

    @Captor
    ArgumentCaptor <Exception> connectErrorCaptor;


    @Before
    public void setUp() {
        super.setUpBefore(false);
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
