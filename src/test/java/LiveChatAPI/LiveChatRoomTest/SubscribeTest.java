package LiveChatAPI.LiveChatRoomTest;

import LiveChatAPI.LiveChatRoomTest.ChatRoomParent.RoomParent;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.livechat.model.GuestObject;
import org.junit.After;
import org.junit.Before;

/**
 * Created by sachin on 17/7/17.
 */
public class SubscribeTest extends RoomParent {

    @Before
    public void setup(){
        setUpBefore();
    }

    @Override
    public void onLogin(GuestObject object, ErrorObject error) {
        super.onLogin(object, error);
    }

    @After
    public void closeTest(){
        System.out.println("Closing the conversation");
        closeConversation();
    }
}
