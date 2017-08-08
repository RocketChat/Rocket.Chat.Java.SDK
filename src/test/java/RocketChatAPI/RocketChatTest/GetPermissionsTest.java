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
import io.rocketchat.core.callback.AccountListener;
import io.rocketchat.core.model.Permission;
import io.rocketchat.core.model.TokenObject;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
public class GetPermissionsTest extends RocketChatParent {
    String username = "testuserrocks";
    String password = "testuserrocks";

    @Mock
    AccountListener.getPermissionsListener listener;

    @Captor
    ArgumentCaptor<ArrayList<Permission>> listArgumentCaptor;

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
        api.getPermissions(listener);
    }

    @Test
    public void getPermissionsTest() {
        Mockito.verify(listener, timeout(12000).atLeastOnce()).onGetPermissions(listArgumentCaptor.capture(), errorArgumentCaptor.capture());
        Assert.assertNotNull(listArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        Assert.assertTrue(listArgumentCaptor.getValue().size() > 0);
    }

    @After
    public void logout() {
        api.logout(null);
    }
}
