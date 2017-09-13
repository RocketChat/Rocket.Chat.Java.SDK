package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sachin on 3/8/17.
 */
public class CreatePublicGroupTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    String groupName = "PUBLICTESTGROUP";

    @Before
    public void setUp() {
        super.setUpBefore();
    }

    @Test(timeout = 12000)
    public void createPublicGroupTest() throws Exception {
        String result = api.singleConnect()
                .thenCompose(v -> api.login(username, password))
                .thenCompose(token -> api.createPublicGroup(groupName, new String[]{}, false))
                .get();
        Assert.assertNotNull(result);
//        Assert.assertNull(errorArgumentCaptor.getValue());
//        System.out.println("Room id is " + roomIdArgumentCaptor.getValue());
    }

    @After
    public void logout() {
        api.logout();
    }
}
