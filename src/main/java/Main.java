import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import com.rocketchat.core.model.attachment.TAttachment;

import java.util.List;

/**
 * Created by sachin on 7/6/17.
 */

public class Main implements
        ConnectListener,
        LoginCallback,
        SimpleListCallback<SubscriptionObject>,
        MessageCallback.SubscriptionCallback {

    private static String serverurl = "ws://localhost:3000";
    private static String baseUrl = "htttps://localhost:3000/";
    RocketChatAPI api;
    RocketChatAPI.ChatRoom room;

    String file_path = "/home/sachin/Pictures/pain.jpg";

    public static void main(String[] args) {
        new Main().call();
    }

    public void call() {
        api = new RocketChatAPI.Builder().websocketUrl(serverurl).restBaseUrl(baseUrl).build();
        /*api.setReconnectionStrategy(new ReconnectionStrategy(4, 2000));
        api.setPingInterval(3000);*/
        api.connect(this);

    }

    public void onLoginSuccess(TokenObject token) {
        api.getSubscriptions(this);
    }

    public void onMessage(String roomId, RocketChatMessage message) {
        System.out.println("Got message " + message.getMessage());
        switch (message.getMsgType()) {
            case TEXT:
                System.out.println("This is a text message");
                break;
            case ATTACHMENT:
                List<TAttachment> attachments = message.getAttachments();
                for (TAttachment attachment : attachments) {
                    switch (attachment.getAttachmentType()) {
                        case TEXT_ATTACHMENT:
                            System.out.println("This is a reply or quote to a message");
                            break;
                        case IMAGE:
                            System.out.println("There is a image attachment");
                            break;
                        case AUDIO:
                            System.out.println("There is a audio attachment");
                            break;
                        case VIDEO:
                            System.out.println("There is a video attachment");
                            break;
                    }
                }
                break;
            case MESSAGE_EDITED:
                System.out.println("Message has been edited");
                break;
            case MESSAGE_STARRED:
                System.out.println("Message is starred now");
                break;
            case MESSAGE_REACTION:
                System.out.println("Got message reaction");
                break;
            case MESSAGE_REMOVED:
                System.out.println("Message is deleted");
                break;
            case ROOM_NAME_CHANGED:
                System.out.println("Room name changed");
                break;
            case ROOM_ARCHIVED:
                System.out.println("Room is archived");
                break;
            case ROOM_UNARCHIVED:
                System.out.println("Room is unarchieved");
                break;
            case USER_ADDED:
                System.out.println("User added to the room");
                break;
            case USER_REMOVED:
                System.out.println("User removed from the room");
                break;
            case USER_JOINED:
                System.out.println("User joined the room");
                break;
            case USER_LEFT:
                System.out.println("User left the room");
                break;
            case USER_MUTED:
                System.out.println("User muted now");
                break;
            case USER_UNMUTED:
                System.out.println("User un-muted now");
                break;
            case WELCOME:
                System.out.println("User welcomed");
                break;
            case SUBSCRIPTION_ROLE_ADDED:
                System.out.println("Subscription role added");
                break;
            case SUBSCRIPTION_ROLE_REMOVED:
                System.out.println("Subscription role removed");
                break;
            case OTHER:
                break;
        }

    }

    public void onConnect(String sessionID) {
        System.out.println("Connected to server");
        api.loginUsingToken("token", this);
    }

    public void onConnectError(Throwable websocketException) {
        System.out.println("Got connect error here");
    }

    public void onDisconnect(boolean closedByServer) {
        System.out.println("Disconnect detected here");
    }

    public void onError(Error error) {
        System.out.println("Error: " + error);
    }

    public void onSuccess(List<SubscriptionObject> subscriptions) {
        ChatRoomFactory factory = api.getChatRoomFactory();
        room = factory.createChatRooms(subscriptions).getChatRoomByName("general");
        room.subscribeRoomMessageEvent(null, this);
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
 *
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 *
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

/**
 * Localhost dummy user: {"userName":"guest-18","roomId":"u7xcgonkr7sh","userId":"rQ2EHbhjryZnqbZxC","visitorToken":"707d47ae407b3790465f61d28ee4c63d","authToken":"VYIvfsfIdBaOy8hdWLNmzsW0yVsKK4213edmoe52133"}
 */

