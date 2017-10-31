package com.rocketchat.sample;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.PaginatedCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.Sort;
import com.rocketchat.core.ChatRoom;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.model.attachment.Attachment;
import com.rocketchat.core.provider.TokenProvider;

import java.util.List;
import java.util.Scanner;

/**
 * This class shows the lists fetched from a Room (e.g. its member list, file list, favorite message list and pinned message list).
 *
 * @author Filipe de Lima Brito (filipedelimabrito@gmail.com)
 */
public class RestApiRoomList {

    public static void main(String[] args) {
        new RestApiRoomList().init();
    }

    private void init() {
        buildClient();
        doLogin();
    }

    // Builds the RocketChatClient.
    private void buildClient() {
        rocketChatClient = new RocketChatClient.Builder()
                .websocketUrl(serverUrl)
                .restBaseUrl(baseUrl)
                .logger(logger)
                .tokenProvider(tokenProvider)
                .build();
        rocketChatClient.setReconnectionStrategy(new ReconnectionStrategy(4, 2000));
        rocketChatClient.setPingInterval(15000);
    }

    // Example of login with REST API.
    private void doLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        System.out.println("Loading...\n\n");

        rocketChatClient.signin(username, password, loginCallback);
        rocketChatClient.connect(connectListener);
    }

    // Example of getting the subscription with the RealTime API.
    private void getSubscriptions() {
        rocketChatClient.getSubscriptions(new SimpleListCallback<Subscription>() {
            @Override
            public void onSuccess(List<Subscription> subscriptions) {
                logger.info("\n\nSuccess getting the subscriptions!");
                logger.info("Subscriptions: " + subscriptions + "\n\n");

                ChatRoomFactory chatRoomFactory = rocketChatClient.getChatRoomFactory();
                ChatRoom room = chatRoomFactory.createChatRooms(subscriptions).getChatRoomByName("general");
                getRoomFilesByRoom(room);
            }

            @Override
            public void onError(RocketChatException error) {
                logger.info("Error on getting the subscriptions. Error: " + error.getMessage());
            }
        });
    }

    // Example of querying the file list from a room.
    private void getRoomFilesByRoom(final ChatRoom room) {
        room.getFiles(0, Attachment.SortBy.UPLOADED_DATE, Sort.DESC, new PaginatedCallback() {
            @Override
            public void onSuccess(List list, long total, long offset) {
                logger.info("\n\nSuccess getting the file list from " + room.getRoomData().name() + " room");
                logger.info("File list total number: " + total);
                logger.info("Files:\n");
                List<Attachment> attachments = list;
                for(Attachment attachment: attachments) {
                    logger.info("File name: " + attachment.getName());
                    logger.info("File type: " + attachment.getType());
                    logger.info("File link: " + attachment.getLink());
                    logger.info("File upload date (timestamp): " + attachment.getUploadedAt());
                    logger.info("\n\n");
                }
            }

            @Override
            public void onError(RocketChatException error) {
                logger.info("Error on getting the file list. Error: " + error.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- ATTRIBUTES ----------------------------------------------------------------
    private RocketChatClient rocketChatClient;
    private Token token;

    private Logger logger = new Logger() {
        @Override
        public void info(String format, Object... args) {
            System.out.println(format);
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

    private TokenProvider tokenProvider = new TokenProvider() {
        @Override
        public void saveToken(Token token) {
            logger.info("Saving token...");
            RestApiRoomList.this.token = token;
            logger.info("Token saved! Token: " + token.authToken() + "\n\n");
        }

        @Override
        public Token getToken() {
            return token;
        }
    };

    private LoginCallback loginCallback = new LoginCallback() {
        @Override
        public void onLoginSuccess(Token token) {
            logger.info("\n\nSuccess on the login!");
            tokenProvider.saveToken(token);
            getSubscriptions();
        }

        @Override
        public void onError(RocketChatException error) {
            logger.warning("Can not login! Error: " + error.getMessage());
        }
    };

    private ConnectListener connectListener = new ConnectListener() {
        public void onConnect(String sessionID) {
            rocketChatClient.loginUsingToken(tokenProvider.getToken().authToken(), loginCallback);
            logger.info("\n\nLogged using token!\n\n");
        }

        public void onConnectError(Throwable websocketException) {
            logger.warning("Connect error! Error: " + websocketException.getMessage() + "\n\n");
        }

        public void onDisconnect(boolean closedByServer) {
            logger.info("Disconnected from the server!\n\n");
        }
    };

    private static final String serverUrl = "wss://open.rocket.chat/websocket";
    private static final String baseUrl = "https://open.rocket.chat/";
    // ---------------------------------------------------------------- ATTRIBUTES ----------------------------------------------------------------
}