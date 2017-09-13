package RocketChatAPI.RocketChatRoomTest;

import RocketChatAPI.RocketChatRoomTest.ChatRoomParent.RoomParent;
import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.SimpleListener;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.callback.MessageListener;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.SubscriptionObject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.timeout;

/**
 * Created by sachin on 3/8/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageOpTest extends RoomParent {

    @Rule
    public TestName testName = new TestName();

    String msgId;

    public void TestThisCode() throws Exception {
        CompletableFuture<RocketChatAPI.ChatRoom> roomResult = getChatRoom();
        Boolean result = roomResult
                .thenCompose(room -> room.sendMessage("Hey there bro"))
                .thenCompose(message -> {
                    msgId = message.getMessageId();
                    if (testName.getMethodName().equals("A_updateMessageTest")) {
                        return roomResult.thenCompose(r -> r.updateMessage(message.getMessageId(), "This is a updated message"));
                    } else if (testName.getMethodName().equals("B_pinMessageTest")) {
                        return roomResult.thenCompose(r -> r.pinMessage(message.getRawJsonObject()));
                    } else if (testName.getMethodName().equals("C_unpinMessageTest")) {
                        return roomResult.thenCompose(r -> r.unpinMessage(message.getRawJsonObject()));
                    } else if (testName.getMethodName().equals("D_starMessageTest")) {
                        return roomResult.thenCompose(r -> r.starMessage(msgId, true));
                    } else if (testName.getMethodName().equals("E_deleteMessageTest")) {
                        return roomResult.thenCompose(r -> r.deleteMessage(msgId));
                    } else {
                        throw new IllegalStateException();
                    }
                })
                .get();

        Assert.assertNotNull(result);
    }

    @Test(timeout = 12000)
    public void A_updateMessageTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void B_pinMessageTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void C_unpinMessageTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void D_starMessageTest() throws Exception {
        TestThisCode();
    }

    @Test(timeout = 12000)
    public void E_deleteMessageTest() throws Exception {
        TestThisCode();
    }

    @Override
    public void logout() throws InterruptedException {
        if (msgId != null) {
            room.deleteMessage(msgId);
        }
        super.logout();
    }
}
