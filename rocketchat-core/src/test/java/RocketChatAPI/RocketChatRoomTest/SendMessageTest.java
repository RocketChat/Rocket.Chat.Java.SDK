package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.core.model.RocketChatMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sachin on 3/8/17.
 */
public class SendMessageTest extends RoomParent {

    @Test(timeout = 12000)
    public void sendMessageTest() throws Exception {
        RocketChatMessage result = getChatRoom().thenCompose(room -> room.sendMessage("Hey there how are you")).get();
        Assert.assertNotNull(result);
        System.out.println("Message is " + result.getMessage());
    }

}
