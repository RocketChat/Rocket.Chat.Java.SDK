package LiveChatAPI;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import LiveChatAPI.LiveChatTest.ConnectionTest;
import LiveChatAPI.LiveChatTest.GetIntitalDataTest;
import LiveChatAPI.LiveChatTest.LoginTest;
import LiveChatAPI.LiveChatTest.RegisterTest;
import LiveChatAPI.LiveChatTest.SendOfflineMessageTest;

/**
 * Created by sachin on 17/7/17.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({ConnectionTest.class, GetIntitalDataTest.class, RegisterTest.class, LoginTest.class, SendOfflineMessageTest.class})

public class LiveChatTestSuite {

}
