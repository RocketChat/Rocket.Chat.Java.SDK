package com.rocketchat.sample;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.data.lightstream.document.UserDocument;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.StreamCollectionListener;
import com.rocketchat.common.listener.SubscribeCallback;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Token;
import org.json.JSONObject;

/**
 * Created by sachin on 7/6/17.
 */

public class Main {

    private static String serverurl = "wss://demo.rocket.chat/websocket";
    private static String baseUrl = "https://demo.rocket.chat/";
    RocketChatClient client;
    String username = "";
    String password = "";
    private Token token;

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


        // Example signin with REST Api, then use the token for websocket login
        client.signin("username", "password", new LoginCallback() {
            @Override
            public void onError(RocketChatException error) {
                error.printStackTrace();
            }

            @Override
            public void onLoginSuccess(Token token) {
                Main.this.token = token;
                client.connect(connectListener);
            }
        });

        client.getGlobalStreamCollectionManager().subscribeUserCollection(new StreamCollectionListener<UserDocument>() {
            @Override
            public void onAdded(String documentKey, UserDocument document) {
                System.out.println("User added "+ document);
            }

            @Override
            public void onChanged(String documentKey, UserDocument values) {
                System.out.println("User values changed " + values.toString());
            }

            @Override
            public void onRemoved(String documentKey) {
                System.out.println("User removed with id "+ documentKey);
            }
        });

    }

    LoginCallback loginCallback = new LoginCallback() {
        @Override
        public void onError(RocketChatException error) {

        }

        @Override
        public void onLoginSuccess(Token token) {
            System.out.println("Login is successful");
            client.subscribeActiveUsers(new SubscribeCallback() {
                @Override
                public void onSubscribe(Boolean isSubscribed, String subId) {
                    if (isSubscribed) {
                        System.out.println("Subscribed for getting active user statuses");
                    }
                }
            });

        }
    };

    ConnectListener connectListener = new ConnectListener() {
        public void onConnect(String sessionID) {
            System.out.println("Connected to server");
            client.login(username, password, loginCallback);
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
            System.out.println(format + args);
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

