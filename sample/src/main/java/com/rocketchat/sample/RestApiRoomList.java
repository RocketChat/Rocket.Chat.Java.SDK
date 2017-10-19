package com.rocketchat.sample;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.Sort;
import com.rocketchat.core.ChatRoom;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.RoomCallback;
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
        getSubscriptions();
        getRoomFiles();
    }

    // Build the RocketChatClient.
    private void buildClient() {
        rocketChatClient = new RocketChatClient.Builder()
                .websocketUrl(serverUrl)
                .restBaseUrl(baseUrl)
                .logger(new Logger() {
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
                })
                .tokenProvider(tokenProvider)
                .build();

        rocketChatClient.setReconnectionStrategy(new ReconnectionStrategy(4, 2000));
        rocketChatClient.setPingInterval(15000);

        chatRoomFactory = rocketChatClient.getChatRoomFactory();
    }

    // Example of login with REST API.
    private void doLogin() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        System.out.println("Loading...");

        rocketChatClient.signin(username, password, new LoginCallback() {
            @Override
            public void onLoginSuccess(Token token) {
                System.out.println("Loaded!\n\n");
                tokenProvider.saveToken(token);
            }

            @Override
            public void onError(RocketChatException error) {
                System.out.println("Error: " + error);
            }
        });
    }

    // Example of getting the subscription with the RealTime API.
    private void getSubscriptions() {
        rocketChatClient.getSubscriptions(new SimpleListCallback<Subscription>() {
            @Override
            public void onSuccess(List<Subscription> subscriptions) {
                room = chatRoomFactory.createChatRooms(subscriptions).getChatRoomByName("general");
            }

            @Override
            public void onError(RocketChatException error) {
                System.out.println("Error: " + error);
            }
        });
    }

    // Example of querying the file list from a room (general in this case).
    private void getRoomFiles() {
        if (room != null) {
            room.getFiles("0", Attachment.SortBy.UPLOADED_DATE, Sort.DESC, new RoomCallback.GetFilesCallback() {
                @Override
                public void onGetRoomFiles(int total, List<Attachment> files) {
                    System.out.println(total);
                }

                @Override
                public void onError(RocketChatException error) {

                }
            });
        }
    }

    // ---------------------------------------------------------------- ATTRIBUTES ----------------------------------------------------------------
    private RocketChatClient rocketChatClient;
    private ChatRoomFactory chatRoomFactory;
    private ChatRoom room;
    private TokenProvider tokenProvider = new TokenProvider() {
        @Override
        public void saveToken(Token token) {
            System.out.println("Token: " + token.getAuthToken());
        }

        @Override
        public Token getToken() {
            return null;
        }
    };
    private static final String serverUrl = "wss://open.rocket.chat/websocket";
    private static final String baseUrl = "https://open.rocket.chat/";
    // ---------------------------------------------------------------- ATTRIBUTES ----------------------------------------------------------------
}