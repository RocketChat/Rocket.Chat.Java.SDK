package RocketChatAPI.RocketChatTest;

import RocketChatAPI.RocketChatTest.ChatParent.RocketChatParent;
import com.rocketchat.core.model.RoomObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by sachin on 2/8/17.
 */
public class GetRoomsTest extends RocketChatParent {

    String username = "testuserrocks";
    String password = "testuserrocks";

    @Before
    public void setUp() {
        super.setUpBefore();
    }

    @Test(timeout = 12000)
    public void getRoomsTest() throws Exception {
        List<RoomObject> result = api.singleConnect()
                .thenCompose(v -> api.login(username, password))
                .thenCompose(token -> api.getRooms())
                .get();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        System.out.println("size of rooms available" + result.size());
    }

    @After
    public void logout() {
        api.logout();
    }
}
