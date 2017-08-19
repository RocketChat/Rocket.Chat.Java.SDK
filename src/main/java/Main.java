import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.adapter.CoreAdapter;
import io.rocketchat.core.callback.UploadListener;
import io.rocketchat.core.model.FileObject;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sachin on 7/6/17.
 */

public class Main extends CoreAdapter {

    private static String serverurl = "wss://demo.rocket.chat/websocket";
    RocketChatAPI api;
    RocketChatAPI.ChatRoom room;

    String file_path = "/home/sachin/Pictures/itachi.png";

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
    }

    @Override
    public void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error) {
        room = api.getChatRoomFactory().createChatRooms(subscriptions).getChatRoomByName("general");
        File file = new File(file_path);
        room.uploadFile(file, file.getName(), "My favourite character", new UploadListener() {
            @Override
            public void onUploadStarted(String roomId, String fileName, String description) {
                System.out.println("Upload now started");
            }

            @Override
            public void onUploadProgress(int progress, String roomId, String fileName, String description) {
                System.out.println("Upload progress is " + progress);
            }

            @Override
            public void onUploadComplete(int statusCode, FileObject file, String roomId, String fileName, String description) {
                System.out.println("Upload is now complete with status code "+ statusCode);
                room.sendFileMessage(file);
            }

            @Override
            public void onUploadError(ErrorObject error, IOException e) {
                System.out.println("This is upload error " + e + " " + error.getMessage());
            }
        });
    }


    @Override
    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.login("testuserrocks", "testuserrocks", this);
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
 *
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 *
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 *
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

