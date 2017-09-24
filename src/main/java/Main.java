import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.adapter.CoreAdapter;
import com.rocketchat.core.db.Document.FileDocument;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sachin on 7/6/17.
 */
// TODO: 09/09/17 add autosubscription scheduler

public class Main extends CoreAdapter {

    String username = "sachin.shinde";
    String password = "sachin123";

    private static String serverurl = "wss://demo.rocket.chat";
    RocketChatAPI api;

    public static void main(String[] args) {
        new Main().call();
    }

    public void call() {
        api = new RocketChatAPI(serverurl);
        api.setReconnectionStrategy(new ReconnectionStrategy(4, 2000));
        api.connect(this);

    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        System.out.println("Logged in successfully");
        api.getSubscriptions(this);
    }

    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login(username, password, this);
    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        api.getChatRoomFactory().createChatRooms(subscriptions);
        final RocketChatAPI.ChatRoom room = api.getChatRoomFactory().getChatRoomByName("general");

        room.getRoomDbManager().getRoomFilesCollection().addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                FileDocument document = (FileDocument) arg;
                System.out.println("document file name is " + document.getFileName());
                System.out.println("document file type is " + document.getFileType());
            }
        });

        room.subscribeRoomFiles(20, null);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Number of files are " + room.getRoomDbManager().getRoomFilesCollection().getData().size());
            }
        },2000);

//        room.subscribeSnipettedMessages(20, null);
//        room.subscribePinnedMessages(20, null);
//        room.subscribeMentionedMessages(20, null);
//        room.subscribeStarredMessages(20, null);
    }


    @Override
    public void onConnectError(Exception websocketException) {
        System.out.println("Got connect error here");
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnect detected here");
    }

}

/**
 * RocketChat server dummy user : {"userName":"guest-3829","roomId":"1hrjr4sruo9q1","userId":"9kAri3uXquAnkMeb4","visitorToken":"-57c7cb8f9c53963712368351705f4d9b","authToken":"qTcmnjIrfQB55bTd9GYhuGOOU63WY0-_afbCe8hyX_r"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 * <p>
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

