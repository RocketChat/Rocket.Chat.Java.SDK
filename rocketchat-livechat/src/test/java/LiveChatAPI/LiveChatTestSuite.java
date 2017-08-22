package LiveChatAPI;

import LiveChatAPI.LiveChatTest.ConnectionTest;
import LiveChatAPI.LiveChatTest.GetIntitalDataTest;
import LiveChatAPI.LiveChatTest.LoginTest;
import LiveChatAPI.LiveChatTest.RegisterTest;
import LiveChatAPI.LiveChatTest.SendOfflineMessageTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by sachin on 17/7/17.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ConnectionTest.class,
        GetIntitalDataTest.class,
        RegisterTest.class,
        LoginTest.class,
        SendOfflineMessageTest.class})

public class LiveChatTestSuite {

}
