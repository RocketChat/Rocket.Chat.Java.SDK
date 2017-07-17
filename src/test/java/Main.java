import LiveChatAPI.LiveChatRoomTestSuite;
import LiveChatAPI.LiveChatTestSuite;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by sachin on 15/7/17.
 */

public class Main {

    public static void main(String [] args ){
        /**
         * It's recommended to run each test suite independently via IDE functionality.
         */
        Result result = JUnitCore.runClasses(LiveChatTestSuite.class, LiveChatRoomTestSuite.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
