package com.rocketchat.core;

import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.SocketListener;
import com.rocketchat.common.data.CommonJsonAdapterFactory;
import com.rocketchat.common.data.TimestampAdapter;
import com.rocketchat.common.data.lightstream.GlobalStreamCollectionManager;
import com.rocketchat.common.data.model.User;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.listener.SubscribeListener;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.network.ConnectivityManager;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.network.SocketFactory;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.NoopLogger;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.callback.RoomCallback;
import com.rocketchat.core.callback.ServerInfoCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.internal.middleware.CoreStreamMiddleware;
import com.rocketchat.core.model.Emoji;
import com.rocketchat.core.model.JsonAdapterFactory;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.PublicSetting;
import com.rocketchat.core.model.Room;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.provider.TokenProvider;
import com.rocketchat.core.uploader.IFileUpload;
import com.squareup.moshi.Moshi;
import java.util.Date;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.json.JSONObject;

import static com.rocketchat.common.utils.Preconditions.checkNotNull;

/**
 * Created by sachin on 8/6/17.
 */

// TODO: 30/7/17 Make it singletone like eventbus, add builder class to RocketChatAPI in order to use it anywhere, maybe a common builder class
public class RocketChatClient {

    private final HttpUrl baseUrl;
    private final OkHttpClient client;
    private final Logger logger;
    private final SocketFactory factory;

    private TokenProvider tokenProvider;
    private RestImpl restImpl;
    private WebsocketImpl websocketImpl;
    private Moshi moshi;

    // chatRoomFactory class
    private ChatRoomFactory chatRoomFactory;
    private GlobalStreamCollectionManager globalStreamCollectionManager;
    private ConnectivityManager connectivityManager;

    private RocketChatClient(final Builder builder) {
        if (builder.baseUrl == null || builder.websocketUrl == null) {
            throw new IllegalStateException("You must provide both restBaseUrl and websocketUrl");
        }
        this.baseUrl = builder.baseUrl;

        if (builder.client == null) {
            client = new OkHttpClient();
        } else {
            client = builder.client;
        }

        if (builder.factory != null) {
            this.factory = builder.factory;
        } else {
            this.factory = new SocketFactory() {
                @Override
                public Socket create(OkHttpClient client, String url, Logger logger, SocketListener socketListener) {
                    return new Socket(client, url, logger, socketListener);
                }
            };
        }

        if (builder.logger != null) {
            this.logger = builder.logger;
        } else {
            this.logger = new NoopLogger();
        }

        // TODO - Add to the Builder
        moshi = new Moshi.Builder()
                .add(new TimestampAdapter())
                .add(JsonAdapterFactory.create())
                .add(CommonJsonAdapterFactory.create())
                .build();

        tokenProvider = builder.provider;

        connectivityManager = new ConnectivityManager();
        chatRoomFactory = new ChatRoomFactory(this);
        globalStreamCollectionManager = new GlobalStreamCollectionManager(moshi);

        restImpl = new RestImpl(client, moshi, baseUrl, tokenProvider, logger);
        websocketImpl = new WebsocketImpl(client, factory, moshi, builder.websocketUrl, logger, chatRoomFactory, globalStreamCollectionManager, connectivityManager);
    }

    public WebsocketImpl getWebsocketImpl() {
        return websocketImpl;
    }

    public String getMyUserName() {
        // TODO - re-implement
        return null;
        //return dbManager.getUserCollection().get(userId).username();
    }

    public String getMyUserId() {
        //return userId;
        // TODO - re-implement
        return null;
    }

    public ChatRoomFactory getChatRoomFactory() {
        return chatRoomFactory;
    }

    public GlobalStreamCollectionManager getGlobalStreamCollectionManager() {
        return globalStreamCollectionManager;
    }


    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }


    public void serverInfo(ServerInfoCallback callback) {
        restImpl.serverInfo(callback);
    }

    public void signin(String username, String password, final LoginCallback loginCallback) {
        restImpl.signin(username, password, loginCallback);
    }

    public void pinMessage(String messageId, SimpleCallback callback) {
        restImpl.pinMessage(messageId, callback);
    }

    public void login(LoginCallback loginCallback) {
        Token token = tokenProvider != null ? tokenProvider.getToken() : null;
        if (token == null) {
            loginCallback.onError(new RocketChatAuthException("Missing token"));
            return;
        }

        loginUsingToken(token.getAuthToken(), loginCallback);
    }

    //Tested
    public void login(String username, String password, LoginCallback loginCallback) {
        websocketImpl.login(username, password, loginCallback);
    }

    //Tested
    public void loginUsingToken(String token, LoginCallback loginCallback) {
        websocketImpl.loginUsingToken(token, loginCallback);
    }

    //Tested
    public void getPermissions(SimpleListCallback<Permission> callback) {
        websocketImpl.getPermissions(callback);
    }

    //Tested
    public void getPublicSettings(SimpleListCallback<PublicSetting> callback) {
        websocketImpl.getPublicSettings(callback);
    }

    //Tested
    public void getUserRoles(SimpleListCallback<User> callback) {
        websocketImpl.getUserRoles(callback);
    }

    //Tested
    public void listCustomEmoji(SimpleListCallback<Emoji> callback) {
        websocketImpl.listCustomEmoji(callback);
    }

    //Tested
    public void logout(SimpleCallback callback) {
        websocketImpl.logout(callback);
    }

    //Tested
    public void getSubscriptions(SimpleListCallback<Subscription> callback) {
        websocketImpl.getSubscriptions(callback);
    }

    //Tested
    public void getRooms(SimpleListCallback<Room> callback) {
        websocketImpl.getRooms(callback);
    }

    //Tested
    void getRoomRoles(String roomId, SimpleListCallback<RoomRole> callback) {
        websocketImpl.getRoomRoles(roomId, callback);
    }

    //Tested
    void getChatHistory(String roomID, int limit, Date oldestMessageTimestamp,
                        Date lasttimestamp, HistoryCallback callback) {
        websocketImpl.getChatHistory(roomID, limit, oldestMessageTimestamp, lasttimestamp, callback);
    }

    void getRoomMembers(String roomID, Boolean allUsers, RoomCallback.GetMembersCallback callback) {
        websocketImpl.getRoomMembers(roomID, allUsers, callback);
    }

    //Tested
    void sendIsTyping(String roomId, String username, Boolean istyping) {
        websocketImpl.sendIsTyping(roomId, username, istyping);
    }

    //Tested
    void sendMessage(String msgId, String roomID, String message, MessageCallback.MessageAckCallback callback) {
        websocketImpl.sendMessage(msgId, roomID, message, callback);
    }

    //Tested
    void deleteMessage(String msgId, SimpleCallback callback) {
        websocketImpl.deleteMessage(msgId, callback);
    }

    //Tested
    void updateMessage(String msgId, String roomId, String message, SimpleCallback callback) {
        websocketImpl.updateMessage(msgId, roomId, message, callback);
    }

    //Tested
    @Deprecated
    void pinMessage(JSONObject message, SimpleCallback callback) {
        websocketImpl.pinMessage(message, callback);
    }

    //Tested
    void unpinMessage(JSONObject message, SimpleCallback callback) {
        websocketImpl.unpinMessage(message, callback);
    }

    //Tested
    void starMessage(String msgId, String roomId, Boolean starred, SimpleCallback callback) {
        websocketImpl.starMessage(msgId, roomId, starred, callback);
    }

    //Tested
    void setReaction(String emojiId, String msgId, SimpleCallback callback) {
        websocketImpl.setReaction(emojiId, msgId, callback);
    }

    void searchMessage(String message, String roomId, int limit,
                       SimpleListCallback<Message> callback) {
        websocketImpl.searchMessage(message, roomId, limit, callback);
    }

    //Tested
    public void createPublicGroup(String groupName, String[] users, Boolean readOnly,
                                  RoomCallback.GroupCreateCallback callback) {
        websocketImpl.createPublicGroup(groupName, users, readOnly, callback);
    }

    //Tested
    public void createPrivateGroup(String groupName, String[] users,
                                   RoomCallback.GroupCreateCallback callback) {
        websocketImpl.createPrivateGroup(groupName, users, callback);
    }

    //Tested
    void deleteGroup(String roomId, SimpleCallback callback) {
        websocketImpl.deleteGroup(roomId, callback);
    }

    //Tested
    void archiveRoom(String roomId, SimpleCallback callback) {
        websocketImpl.archiveRoom(roomId, callback);
    }

    //Tested
    void unarchiveRoom(String roomId, SimpleCallback callback) {
        websocketImpl.unarchiveRoom(roomId, callback);
    }

    //Tested
    public void joinPublicGroup(String roomId, String joinCode, SimpleCallback callback) {
        websocketImpl.joinPublicGroup(roomId, joinCode, callback);
    }

    //Tested
    void leaveGroup(String roomId, SimpleCallback callback) {
        websocketImpl.leaveGroup(roomId, callback);
    }

    //Tested
    void hideRoom(String roomId, SimpleCallback callback) {
        websocketImpl.hideRoom(roomId, callback);
    }

    //Tested
    void openRoom(String roomId, SimpleCallback callback) {
        websocketImpl.openRoom(roomId, callback);
    }

    //Tested
    void setFavouriteRoom(String roomId, Boolean isFavouriteRoom, SimpleCallback callback) {
        websocketImpl.setFavouriteRoom(roomId, isFavouriteRoom, callback);
    }

    void sendFileMessage(String roomId, String store, String fileId, String fileType,
                         int size, String fileName, String desc, String url,
                         MessageCallback.MessageAckCallback callback) {
        websocketImpl.sendFileMessage(roomId, store, fileId, fileType, size, fileName, desc, url,
                callback);
    }

    //Tested
    public void setStatus(User.Status s, SimpleCallback callback) {
        websocketImpl.setStatus(s, callback);
    }

    public void subscribeActiveUsers(SubscribeListener subscribeListener) {
        websocketImpl.subscribeActiveUsers(subscribeListener);
    }

    public void subscribeUserData(SubscribeListener subscribeListener) {
        websocketImpl.subscribeUserData(subscribeListener);
    }

    public void subscribeUserRoles(SubscribeListener subscribeListener) {
        websocketImpl.subscribeUserRoles(subscribeListener);
    }

    public void subscribeLoginConf(SubscribeListener subscribeListener) {
        websocketImpl.subscribeLoginConf(subscribeListener);
    }

    public void subscribeClientVersions(SubscribeListener subscribeListener) {
        websocketImpl.subscribeClientVersions(subscribeListener);
    }

    String subscribeRoomFiles(String roomId, int limit, SubscribeListener listener) {
        return websocketImpl.subscribeRoomFiles(roomId, limit, listener);
    }

    String subscribeMentionedMessages(String roomId, int limit, SubscribeListener listener) {
        return websocketImpl.subscribeMentionedMessages(roomId, limit, listener);
    }

    String subscribeStarredMessages(String roomId, int limit, SubscribeListener listener) {
        return websocketImpl.subscribeStarredMessages(roomId, limit, listener);
    }

    String subscribePinnedMessages(String roomId, int limit, SubscribeListener listener) {
        return websocketImpl.subscribePinnedMessages(roomId, limit, listener);
    }

    String subscribeSnipettedMessages(String roomId, int limit, SubscribeListener listener) {
        return websocketImpl.subscribeSnipettedMessages(roomId, limit, listener);
    }

    //Tested
    String subscribeRoomMessageEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, MessageCallback.MessageListener listener) {
        return websocketImpl.subscribeRoomMessageEvent(roomId, enable, subscribeListener, listener);
    }

    String subscribeRoomTypingEvent(String roomId, Boolean enable, SubscribeListener subscribeListener, TypingListener listener) {
        return websocketImpl.subscribeRoomTypingEvent(roomId, enable, subscribeListener, listener);
    }

    String subscribeRoomDeleteEvent(String roomId, boolean enable, SubscribeListener subscribeListener) {
        return websocketImpl.subscribeRoomDeleteEvent(roomId, enable, subscribeListener);
    }

    void unsubscribeRoom(String subId, SubscribeListener subscribeListener) {
        websocketImpl.unsubscribeRoom(subId, subscribeListener);
    }

    public void createUFS(String fileName, int fileSize, String fileType, String roomId, String description, String store, IFileUpload.UfsCreateCallback listener) {
        websocketImpl.createUFS(fileName, fileSize, fileType, roomId, description, store, listener);
    }

    public void completeUFS(String fileId, String store, String token, IFileUpload.UfsCompleteListener listener) {
        websocketImpl.completeUFS(fileId, store, token, listener);
    }

    public void connect(ConnectListener connectListener) {
        websocketImpl.connect(connectListener);
    }

    public void disconnect() {
        websocketImpl.disconnect();
    }

    public void setReconnectionStrategy(ReconnectionStrategy strategy) {
        websocketImpl.setReconnectionStrategy(strategy);
    }

    public void setPingInterval(long interval) {
        websocketImpl.setPingInterval(interval);
    }

    public void disablePing() {
        websocketImpl.disablePing();
    }

    public void enablePing() {
        websocketImpl.enablePing();
    }

    void removeSubscription(String roomId, CoreStreamMiddleware.SubscriptionType type) {
        websocketImpl.removeSubscription(roomId, CoreStreamMiddleware.SubscriptionType.SUBSCRIBE_ROOM_MESSAGE);
    }

    void removeAllSubscriptions(String roomId) {
        websocketImpl.removeAllSubscriptions(roomId);
    }


    public static final class Builder {
        private String websocketUrl;
        private HttpUrl baseUrl;
        private OkHttpClient client;
        private SocketFactory factory;
        private TokenProvider provider;
        private Logger logger;

        public Builder websocketUrl(String url) {
            this.websocketUrl = checkNotNull(url, "url == null");
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = checkNotNull(client, "client must be non null");
            return this;
        }

        public Builder socketFactory(SocketFactory factory) {
            this.factory = checkNotNull(factory, "factory == null");
            return this;
        }

        public Builder restBaseUrl(String url) {
            checkNotNull(url, "url == null");
            HttpUrl httpUrl = HttpUrl.parse(url);
            if (httpUrl == null) {
                throw new IllegalArgumentException("Illegal URL: " + url);
            }
            return restBaseUrl(httpUrl);
        }

        /**
         * Set the API base URL.
         * <p>
         * The specified endpoint values (such as with {@link @GET}) are resolved against this
         * value using {@link HttpUrl#resolve(String)}. The behavior of this matches that of an
         * {@code <a href="">} link on a website resolving on the current URL.
         * <p>
         * <b>Base URLs should always end in {@code /}.</b>
         * <p>
         * A trailing {@code /} ensures that endpoints values which are relative paths will correctly
         * append themselves to a base which has path components.
         * <p>
         * <b>Correct:</b><br>
         * Base URL: http://example.com/api/<br>
         * Endpoint: foo/bar/<br>
         * Result: http://example.com/api/foo/bar/
         * <p>
         * <b>Incorrect:</b><br>
         * Base URL: http://example.com/api<br>
         * Endpoint: foo/bar/<br>
         * Result: http://example.com/foo/bar/
         * <p>
         * This method enforces that {@code baseUrl} has a trailing {@code /}.
         * <p>
         * <b>Endpoint values which contain a leading {@code /} are absolute.</b>
         * <p>
         * Absolute values retain only the host from {@code baseUrl} and ignore any specified path
         * components.
         * <p>
         * Base URL: http://example.com/api/<br>
         * Endpoint: /foo/bar/<br>
         * Result: http://example.com/foo/bar/
         * <p>
         * Base URL: http://example.com/<br>
         * Endpoint: /foo/bar/<br>
         * Result: http://example.com/foo/bar/
         * <p>
         * <b>Endpoint values may be a full URL.</b>
         * <p>
         * Values which have a host replace the host of {@code baseUrl} and values also with a scheme
         * replace the scheme of {@code baseUrl}.
         * <p>
         * Base URL: http://example.com/<br>
         * Endpoint: https://github.com/square/retrofit/<br>
         * Result: https://github.com/square/retrofit/
         * <p>
         * Base URL: http://example.com<br>
         * Endpoint: //github.com/square/retrofit/<br>
         * Result: http://github.com/square/retrofit/ (note the scheme stays 'http')
         */
        private Builder restBaseUrl(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "baseUrl == null");
            List<String> pathSegments = baseUrl.pathSegments();
            if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
                throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
            }
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder tokenProvider(TokenProvider provider) {
            this.provider = checkNotNull(provider, "provider == null");
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = checkNotNull(logger, "logger == null");
            return this;
        }

        public RocketChatClient build() {
            return new RocketChatClient(this);
        }
    }
}
