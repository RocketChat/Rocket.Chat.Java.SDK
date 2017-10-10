package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.result.LoadHistoryResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Created by sachin on 3/8/17.
 */
public class GetChatHistoryTest extends RoomParent {

    @Test(timeout = 12000)
    public void getChatHistoryTest() throws Exception {
        LoadHistoryResult result = getChatRoom().thenCompose(room -> room.getChatHistory(10, new Date(), null)).get();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getList().size() > 0);

        for (RocketChatMessage message : result.getList()) {
            System.out.println("Message is " + message.getMessage());
        }
    }

}
