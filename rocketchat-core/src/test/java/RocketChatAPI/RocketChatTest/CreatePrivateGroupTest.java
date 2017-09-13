package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sachin on 3/8/17.
 */
public class CreatePrivateGroupTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    String groupName = "PRIVATETESTGROUP";

    @Before
    public void setUp() {
        super.setUpBefore();
    }

    @Test(timeout = 12000)
    public void createPrivateGroupTest() throws Exception {
        String result = api.singleConnect()
                .thenCompose(v -> api.login(username, password))
                .thenCompose(token -> api.createPrivateGroup(groupName, new String[]{}))
                .get();
//        Assert.assertNotNull(roomIdArgumentCaptor.getValue());
//        Assert.assertNull(errorArgumentCaptor.getValue());
//        System.out.println("Room id is " + roomIdArgumentCaptor.getValue());
    }

    @After
    public void logout() {
        api.logout();
    }
}
