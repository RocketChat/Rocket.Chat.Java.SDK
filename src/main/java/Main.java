import io.rocketchat.common.data.lightdb.document.UserDocument;
import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sachin on 7/6/17.
 */

public class Main extends CoreAdapter {

    private static String serverurl = "wss://demo.rocket.chat/websocket";
    RocketChatAPI api;

    public static void main(String[] args) {
        new Main().call();
    }

    public void call() {
            api = new RocketChatAPI(serverurl);
            api.setReconnectionStrategy(null);
            api.setPingInterval(3000);
            api.connect(this);
    }

    @Override
    public void onLogin(TokenObject token, ErrorObject error) {
        api.getSubscriptions(this);
        api.subscribeActiveUsers(null);
        api.getDbManager().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg != null) {
                    UserDocument document = (UserDocument) arg;
                    System.out.println("UserName is "+ document.getUserName());
                    System.out.println("UserId is "+ document.getUserId());
                    System.out.println("New status is "+ document.getStatus());
                }
            }
        });
    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        for (SubscriptionObject subscription : subscriptions) {
            System.out.println("name is "+ subscription.getRoomName());
        }
    }



    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login("testuserrocks","testuserrocks",this);
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
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

