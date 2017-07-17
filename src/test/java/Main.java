import LiveChatAPI.LiveChatTestSuite;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by sachin on 15/7/17.
 */

public class Main {

    public static String serverurl="wss://livechattest.rocket.chat/websocket";

    public static void main(String [] args ){
        Result result = JUnitCore.runClasses(LiveChatTestSuite.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
