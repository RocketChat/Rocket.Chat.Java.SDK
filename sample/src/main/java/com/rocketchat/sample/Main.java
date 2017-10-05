package com.rocketchat.sample;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.core.ChatRoom;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.Message;
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
        client.connect(connectListener);

    }

    ConnectListener connectListener = new ConnectListener() {
        public void onConnect(String sessionID) {
            System.out.println("Connected to server");
            client.login("sachin.shinde", "sachin123", new LoginCallback() {
                @Override
                public void onLoginSuccess(Token token) {
                    System.out.println("Login is successful");
                    client.getSubscriptions(new SimpleListCallback<Subscription>() {
                        @Override
                        public void onSuccess(List<Subscription> list) {
                            ChatRoomFactory factory = client.getChatRoomFactory();
                            factory.createChatRooms(list);
                            ChatRoom chatRoom = factory.getChatRoomByName("general");
                            chatRoom.getChatHistory(20, null, null, new HistoryCallback() {
                                @Override
                                public void onLoadHistory(List<Message> list, int unreadNotLoaded) {
                                    System.out.println("got data here" + list.size());
                                    for (Message message : list) {
                                        System.out.println("Message is " + message.message());
                                    }
                                }

                                @Override
                                public void onError(RocketChatException error) {

                                }
                            });
                        }

                        @Override
                        public void onError(RocketChatException error) {

                        }
                    });
                }

                @Override
                public void onError(RocketChatException error) {

                }
            });

        }

        public void onConnectError(Throwable websocketException) {
            System.out.println("Got connect error here");
        }

        public void onDisconnect(boolean closedByServer) {
            System.out.println("Disconnect detected here");
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

