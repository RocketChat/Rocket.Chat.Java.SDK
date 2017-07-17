package LiveChatAPI;

import LiveChatAPI.LiveChatTest.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by sachin on 17/7/17.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        LiveChatConnectionTest.class,
        GetIntitalDataTest.class,
        RegisterTest.class,
        LoginTest.class,
        SendOfflineMessageTest.class
})
public class LiveChatTestSuite {

}
