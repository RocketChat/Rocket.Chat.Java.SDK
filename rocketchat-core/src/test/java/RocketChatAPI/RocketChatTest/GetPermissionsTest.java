package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import com.rocketchat.core.model.Permission;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by sachin on 3/8/17.
 */
public class GetPermissionsTest extends RocketChatParent {
    String username = "testuserrocks";
    String password = "testuserrocks";

    @Before
    public void setUp() {
        super.setUpBefore();
    }

    @Test(timeout = 12000)
    public void getPermissionsTest() throws Exception {
        List<Permission> result = api.singleConnect()
                .thenCompose(v -> api.login(username, password))
                .thenCompose(token -> api.getPermissions())
                .get();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
    }

    @After
    public void logout() {
        api.logout();
    }
}
