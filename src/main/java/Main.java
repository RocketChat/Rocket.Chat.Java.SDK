import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.adapter.CoreAdapter;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import com.rocketchat.core.model.attachment.Attachment;
import com.rocketchat.core.model.attachment.TAttachment;

import java.util.List;

/**
 * Created by sachin on 7/6/17.
 */
// TODO: 09/09/17 add autosubscription scheduler

public class Main extends CoreAdapter {

    String username = "";
    String password = "";

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
        RocketChatAPI.ChatRoom room = api.getChatRoomFactory().getChatRoomByName("general");
        room.subscribeRoomMessageEvent(new SubscribeListener() {
            public void onSubscribe(Boolean isSubscribed, String subId) {
                System.out.println("Subscribed to room successfully");
            }
        }, this);
    }

    @Override
    public void onMessage(String roomId, RocketChatMessage message) {
        switch (message.getMsgType()) {
            case TEXT:
                System.out.println("This is a text message");
                break;
            case ATTACHMENT:
                List<TAttachment> attachments = message.getAttachments();
                for (TAttachment attachment : attachments) {
                    switch (attachment.getAttachmentType()) {
                        case TEXT_ATTACHMENT:
                            Attachment.TextAttachment textAttachment = (Attachment.TextAttachment) attachment;
                            System.out.println("Message is " + textAttachment.getText());
                            System.out.println("This is a reply or quote to a message");
                            break;
                        case IMAGE:
                            System.out.println("This is a image attachment");
                            Attachment.ImageAttachment imageAttachment = (Attachment.ImageAttachment) attachment;
                            System.out.println("Attachment title is " + imageAttachment.getTitle());
                            System.out.println("Attachment description is " + imageAttachment.getDescription());
                            System.out.println("Image url is " + imageAttachment.getImage_url());
                            System.out.println("Image type is " + imageAttachment.getImage_type());
                            break;
                        case AUDIO:
                            System.out.println("There is a audio attachment");
                            Attachment.AudioAttachment audioAttachment = (Attachment.AudioAttachment) attachment;
                            System.out.println("Attachment title is " + audioAttachment.getTitle());
                            System.out.println("Attachment description is " + audioAttachment.getDescription());
                            System.out.println("Audio url is " + audioAttachment.getAudio_url());
                            System.out.println("Audio type is " + audioAttachment.getAudio_type());
                            break;
                        case VIDEO:
                            System.out.println("There is a video attachment");
                            Attachment.VideoAttachment videoAttachment = (Attachment.VideoAttachment) attachment;
                            System.out.println("Attachment title is " + videoAttachment.getTitle());
                            System.out.println("Attachment description is " + videoAttachment.getDescription());
                            System.out.println("Video url is " + videoAttachment.getVideo_url());
                            System.out.println("Video type is " + videoAttachment.getVideo_type());
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

