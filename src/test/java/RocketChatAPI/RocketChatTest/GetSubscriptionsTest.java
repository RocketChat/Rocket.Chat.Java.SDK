package RocketChatAPI.RocketChatTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.callback.GetSubscriptionListener;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class GetSubscriptionsTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    @Mock
    GetSubscriptionListener listener;

    @Captor
    ArgumentCaptor<ArrayList<SubscriptionObject>> listArgumentCaptor;
    @Captor
    ArgumentCaptor<ErrorObject> errorArgumentCaptor;

    @Before
    public void setUp() {
        super.setUpBefore(true);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected successfully");
        api.login(username, password, this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(listener);
    }

    @Test
    public void getSubscriptionsTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onGetSubscriptions(listArgumentCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(listArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        Assert.assertTrue(listArgumentCaptor.getValue().size() > 0);
    }

    @After
    public void logout() {
        api.logout(null);
    }
}
