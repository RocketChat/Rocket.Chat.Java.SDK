package com.rocketchat.sample;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.data.model.ServerInfo;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.core.ChatRoom;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.callback.ServerInfoCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.model.PublicSetting;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;

import java.util.List;

/**
 * Created by sachin on 7/6/17.
 */

public class Main {

    private static String serverurl = "ws://demo.rocket.chat/websocket";
    private static String baseUrl = "https://demo.rocket.chat/";
    RocketChatClient client;
    ChatRoom room;

    Token token;

    String file_path = "/home/sachin/Pictures/pain.jpg";

    public static void main(String[] args) {
        new Main().call();
    }

    public void call() {
        client = new RocketChatClient.Builder()
                .websocketUrl(serverurl)
                .restBaseUrl(baseUrl)
                .logger(logger)
                .build();
        client.setReconnectionStrategy(new ReconnectionStrategy(4, 2000));
        client.setPingInterval(15000);


        // Example login with REST Api, then use the token for websocket login
        client.signin("username", "password", new LoginCallback() {
            @Override
            public void onLoginSuccess(Token token) {
                Main.this.token = token;
                client.connect(connectListener);
            }

            @Override
            public void onError(RocketChatException error) {
                System.out.println("Error: " + error);
            }
        });

        client.serverInfo(new ServerInfoCallback() {
            @Override
            public void onServerInfo(ServerInfo info) {
                System.out.println("ServerInfo: " + info);
            }

            @Override
            public void onError(RocketChatException error) {
                System.out.println("Error: " + error);
            }
        });
    }

    ConnectListener connectListener = new ConnectListener() {
        public void onConnect(String sessionID) {
            System.out.println("Connected to server");
            client.getPublicSettings(settingsCallback);
            client.loginUsingToken(token.getAuthToken(), loginCallback);
        }

        public void onConnectError(Throwable websocketException) {
            System.out.println("Got connect error here");
        }

        public void onDisconnect(boolean closedByServer) {
            System.out.println("Disconnect detected here");
        }
    };

    SimpleListCallback<PublicSetting> settingsCallback = new SimpleListCallback<PublicSetting>() {
        @Override
        public void onSuccess(List<PublicSetting> list) {
            for (PublicSetting setting : list) {
                System.out.println("Setting: " + setting);
            }
        }

        @Override
        public void onError(RocketChatException error) {

        }
    };

    public LoginCallback loginCallback = new LoginCallback() {
        @Override
        public void onLoginSuccess(Token token) {
            client.getSubscriptions(subscriptionsCallback);
        }

        @Override
        public void onError(RocketChatException error) {
            System.out.println("Error: " + error);
        }
    };

    public SimpleListCallback<Subscription> subscriptionsCallback = new SimpleListCallback<Subscription>() {
        @Override
        public void onSuccess(List<Subscription> subscriptions) {
            ChatRoomFactory factory = client.getChatRoomFactory();
            room = factory.createChatRooms(subscriptions).getChatRoomByName("general");
            room.subscribeRoomMessageEvent(null, messageCallback);
            room.getChatHistory(100, null, null, new HistoryCallback() {
                @Override
                public void onLoadHistory(List<Message> list, int unreadNotLoaded) {
                    for (Message message : list) {
                        System.out.println(message);
                    }
                }

                @Override
                public void onError(RocketChatException error) {
                    error.printStackTrace();
                }
            });
        }

        @Override
        public void onError(RocketChatException error) {
            System.out.println("Error: " + error);
        }
    };

    public MessageCallback.SubscriptionListener messageCallback = new MessageCallback.SubscriptionListener() {
        public void onMessage(String roomId, Message message) {
            System.out.println("Got message " + message.message());
            switch (message.getMsgType()) {
                case TEXT:
                    System.out.println("This is a text message");
                    break;
                /*case ATTACHMENT:
                    List<TAttachment> attachments = message.attachments();
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
                    break;*/
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
    };

    private Logger logger = new Logger() {
        @Override
        public void info(String format, Object... args) {
            System.out.println(String.format(format, args));
        }

        @Override
        public void warning(String format, Object... args) {
            System.out.println(String.format(format, args));
        }

        @Override
        public void debug(String format, Object... args) {
            System.out.println(String.format(format, args));
        }
    };
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

