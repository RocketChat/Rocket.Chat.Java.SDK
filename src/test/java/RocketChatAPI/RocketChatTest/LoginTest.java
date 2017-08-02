package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.model.TokenObject;
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
public class LoginTest extends RocketChatParent{

    String username="testuserrocks";
    String password="testuserrocks";

    @Mock
    LoginListener listener;

    @Mock
    LoginListener resumeListener;

    @Captor
    ArgumentCaptor<TokenObject> tokenArgumentCaptor;

    @Captor
    ArgumentCaptor <ErrorObject> errorArgumentCaptor;


    @Before
    public void setUp() {
        super.setUpBefore(true);
    }


    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected successfully");
        api.login(username,password,listener);
    }

    @Test
    public void loginTest(){
        Mockito.verify(listener, timeout(8000).atLeastOnce()).onLogin(tokenArgumentCaptor.capture(),errorArgumentCaptor.capture());
        Assert.assertNotNull(tokenArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());
        String token=tokenArgumentCaptor.getValue().getAuthToken();
        System.out.println("value of token is "+token);


        api.loginUsingToken(token,resumeListener);

        Mockito.verify(resumeListener, timeout(8000).atLeastOnce()).onLogin(tokenArgumentCaptor.capture(),errorArgumentCaptor.capture());
        Assert.assertNotNull(tokenArgumentCaptor.getValue());
        Assert.assertNull(errorArgumentCaptor.getValue());

        token=tokenArgumentCaptor.getValue().getAuthToken();
        System.out.println("value of token is "+token);
    }

}
