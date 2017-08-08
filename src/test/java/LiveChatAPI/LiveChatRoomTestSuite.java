package LiveChatAPI;

/**
 * Created by sachin on 17/7/17.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import LiveChatAPI.LiveChatRoomTest.CloseConversationTest;
import LiveChatAPI.LiveChatRoomTest.GetAgentDataTest;
import LiveChatAPI.LiveChatRoomTest.GetChatHistoryTest;
import LiveChatAPI.LiveChatRoomTest.LoginTest;
import LiveChatAPI.LiveChatRoomTest.SendMessageTest;
import LiveChatAPI.LiveChatRoomTest.SubscribeTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({LoginTest.class, GetAgentDataTest.class, SubscribeTest.class, GetChatHistoryTest.class, SendMessageTest.class, CloseConversationTest.class})

public class LiveChatRoomTestSuite {

}
