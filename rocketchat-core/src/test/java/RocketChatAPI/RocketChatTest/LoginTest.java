package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import com.rocketchat.core.model.TokenObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sachin on 2/8/17.
 */
public class LoginTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    @Before
    public void setUp() {
        super.setUpBefore();
    }

    @Test(timeout = 16000)
    public void loginTest() throws Exception {
        TokenObject result = api.singleConnect()
                .thenCompose(v -> api.login(username, password))
                .get();

        Assert.assertNotNull(result);
        String token = result.getAuthToken();
        System.out.println("value of token is " + token);

        TokenObject result2 = api.loginUsingToken(token).get();

        Assert.assertNotNull(result2);

        token = result2.getAuthToken();
        System.out.println("value of token is " + token);
    }

    @After
    public void logout() {
        api.logout();
    }

}
