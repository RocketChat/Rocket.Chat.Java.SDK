package com.rocketchat.core;

import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.SocketListener;
import com.rocketchat.common.data.AsStringAdapter;
import com.rocketchat.common.data.CommonJsonAdapterFactory;
import com.rocketchat.common.data.ISO8601Converter;
import com.rocketchat.common.data.TimestampAdapter;
import com.rocketchat.common.data.lightstream.GlobalStreamCollectionManager;
import com.rocketchat.common.data.model.BaseRoom;
import com.rocketchat.common.data.model.BaseUser;
import com.rocketchat.common.data.model.User;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.PaginatedCallback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.listener.SubscribeCallback;
import com.rocketchat.common.listener.TypingListener;
import com.rocketchat.common.network.ConnectivityManager;
import com.rocketchat.common.network.ReconnectionStrategy;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.network.SocketFactory;
import com.rocketchat.common.utils.CalendarISO8601Converter;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.NoopLogger;
import com.rocketchat.common.utils.Sort;
import com.rocketchat.core.annotation.MissingRestMethod;
import com.rocketchat.core.annotation.MoveToRest;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.callback.RoomCallback;
import com.rocketchat.core.callback.ServerInfoCallback;
import com.rocketchat.core.factory.ChatRoomFactory;
import com.rocketchat.core.internal.middleware.CoreStreamMiddleware;
import com.rocketchat.core.internal.model.RestResult;
import com.rocketchat.core.model.JsonAdapterFactory;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.Setting;
import com.rocketchat.core.model.Room;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.model.attachment.Attachment;
import com.rocketchat.core.provider.TokenProvider;
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
    private final ISO8601Converter dateConverter;

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
                public Socket create(OkHttpClient client, String url, Logger logger,
                                     SocketListener socketListener) {
                    return new Socket(client, url, logger, socketListener);
                }
            };
        }

        if (builder.logger != null) {
            this.logger = builder.logger;
        } else {
            this.logger = new NoopLogger();
        }

        if (builder.dateConverter != null) {
            dateConverter = builder.dateConverter;
        } else {
            dateConverter = new CalendarISO8601Converter();
        }

        // TODO - Add to the Builder
        moshi = new Moshi.Builder()
                .add(new AsStringAdapter())
                .add(new TimestampAdapter(dateConverter))
                .add(JsonAdapterFactory.create())
                .add(CommonJsonAdapterFactory.create())
                .add(new RestResult.JsonAdapterFactory())
                .build();

        connectivityManager = new ConnectivityManager();
        chatRoomFactory = new ChatRoomFactory(this);
        globalStreamCollectionManager = new GlobalStreamCollectionManager(moshi);

        tokenProvider = builder.provider;
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

    public Moshi getMoshi() {
        return moshi;
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public void signin(String username, String password, final LoginCallback loginCallback) {
        restImpl.signin(username, password, loginCallback);
    }

    public void serverInfo(ServerInfoCallback callback) {
        restImpl.serverInfo(callback);
    }

    /**
     * Gets all of the private groups the calling user has joined.
     *
     * <p>Example of expected usage:
     *
     * <blockquote><pre>
     * rocketChatClient.getUserGroupList(new SimpleListCallback() {
     *     public void onSuccess(List list) {
     *         // Handle the user group list.
     *     }
     *
     *     public void onError(RocketChatException error) {
     *        // Handle the error.
     *     }
     * });
     * </pre></blockquote>
     *
     * @param callback The simple list callback.
     * @see #getUserChannelList(SimpleListCallback)
     * @see #getUserDirectMessageList(SimpleListCallback)
     * @since 0.8.0
     */
    public void getUserGroupList(SimpleListCallback callback) {
        restImpl.getUserGroupList(callback);
    }

    /**
     * Gets all of the channels the calling user has joined.
     *
     * <p>Example of expected usage:
     *
     * <blockquote><pre>
     * rocketChatClient.getUserChannelList(new SimpleListCallback() {
     *     public void onSuccess(List list) {
     *         // Handle the user channel list.
     *     }
     *
     *     public void onError(RocketChatException error) {
     *        // Handle the error.
     *     }
     * });
     * </pre></blockquote>
     *
     * @param callback The simple list callback.
     * @see #getUserGroupList(SimpleListCallback)
     * @see #getUserDirectMessageList(SimpleListCallback)
     * @since 0.8.0
     */
    public void getUserChannelList(SimpleListCallback callback) {
        restImpl.getUserChannelList(callback);
    }

    /**
     * Gets all of the direct messages the calling user has joined.
     *
     * <p>Example of expected usage:
     *
     * <blockquote><pre>
     * rocketChatClient.getUserDirectMessageList(new SimpleListCallback() {
     *     public void onSuccess(List list) {
     *         // Handle the direct message list.
     *     }
     *
     *     public void onError(RocketChatException error) {
     *        // Handle the error.
     *     }
     * });
     * </pre></blockquote>
     *
     * @param callback The simple list callback.
     * @see #getUserGroupList(SimpleListCallback)
     * @see #getUserChannelList(SimpleListCallback)
     * @since 0.8.0
     */
    public void getUserDirectMessageList(SimpleListCallback callback) {
        restImpl.getUserDirectMessageList(callback);
    }

    public void pinMessage(String messageId, SimpleCallback callback) {
        restImpl.pinMessage(messageId, callback);
    }

    public void getRoomMembers(String roomId,
                               BaseRoom.RoomType roomType,
                               int offset,
                               BaseUser.SortBy sortBy,
                               Sort sort,
                               final PaginatedCallback<User> callback) {
        restImpl.getRoomMembers(roomId, roomType, offset, sortBy, sort, callback);
    }

    public void getRoomFavoriteMessages(String roomId,
                                        BaseRoom.RoomType roomType,
                                        int offset,
                                        final PaginatedCallback callback) {
        restImpl.getRoomFavoriteMessages(roomId, roomType, offset, callback);
    }

    public void getRoomPinnedMessages(String roomId,
                                      BaseRoom.RoomType roomType,
                                      int offset,
                                      final PaginatedCallback<Message> callback) {
        restImpl.getRoomPinnedMessages(roomId, roomType, offset, callback);
    }

    public void getRoomFiles(String roomId,
                             BaseRoom.RoomType roomType,
                             int offset,
                             Attachment.SortBy sortBy,
                             Sort sort,
                             final PaginatedCallback<Attachment> callback) {
        restImpl.getRoomFiles(roomId, roomType, offset, sortBy, sort, callback);
    }

    public void login(LoginCallback loginCallback) {
        Token token = tokenProvider != null ? tokenProvider.getToken() : null;
        if (token == null) {
            loginCallback.onError(new RocketChatAuthException("Missing token"));
            return;
        }

        loginUsingToken(token.authToken(), loginCallback);
    }

    //Tested
    public void loginUsingToken(String token, LoginCallback loginCallback) {
        websocketImpl.loginUsingToken(token, loginCallback);
    }

    @MoveToRest
    @MissingRestMethod
    public void getPermissions(SimpleListCallback<Permission> callback) {
        websocketImpl.getPermissions(callback);
    }

    public void getSettings(PaginatedCallback<Setting> callback) {
        getSettings(0, callback);
    }

    /**
     * Get public Setting's from the Server
     *
     * <p>Example of expected usage:
     *
     * <blockquote><pre>
     * client.getSettings(0, new PaginatedCallback<Setting>() {
     *     public void onSuccess(List<Setting> list, long offset, long total) {
     *         // list with the returned Setting's, current offset, and total number of settings
     *     }
     *
     *     public void onError(RocketChatException error) {
     *        // Handle the error like showing a message to the user
     *     }
     * });
     * </pre></blockquote>
     *
     * @param offset The number of items to “skip” in the query, is zero based so it starts off at 0 being the first item.
     * @param callback The paginated callback.
     * @see Setting
     * @since 0.8.0
     */
    public void getSettings(int offset, PaginatedCallback<Setting> callback) {
        restImpl.getSettings(offset, callback);
    }

    @MoveToRest
    @MissingRestMethod
    public void getUserRoles(SimpleListCallback<User> callback) {
        throw new UnsupportedOperationException("get user roles not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    public void listCustomEmoji() {
        throw new UnsupportedOperationException("get custom emojis not implemented");
    }

    public void logout(SimpleCallback callback) {
        websocketImpl.logout(callback);
    }

    @MoveToRest(discuss = true)
    @MissingRestMethod
    public void getSubscriptions(SimpleListCallback<Subscription> callback) {
        websocketImpl.getSubscriptions(callback);
    }

    @MoveToRest(methods = {
            "/api/v1/channels.list.joinned",
            "/api/v1/groups.list",
            "/api/v1/dm.list"
    })
    public void getRooms(SimpleListCallback<Room> callback) {
        throw new UnsupportedOperationException("getRooms not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    void getRoomRoles(String roomId) {
        throw new UnsupportedOperationException("get Room roles not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.history",
            "/api/v1/groups.history",
            "/api/v1/dm.history"
    })
    void getChatHistory(String roomID, int limit, long oldestMessage,
                        long lastTimestamp) {
        throw new UnsupportedOperationException("get chat history not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    void sendIsTyping(String roomId, String username, Boolean istyping) {
        throw new UnsupportedOperationException("send typing event not implemented");
    }

    @MoveToRest(method = "/api/v1/chat.postMessage")
    void sendMessage(String roomID, String message) {
        throw new UnsupportedOperationException("send message not implemented");
    }

    @MoveToRest(method = "/api/v1/chat.delete")
    void deleteMessage(String msgId, SimpleCallback callback) {
        throw new UnsupportedOperationException("delete message not implemented");
    }

    @MoveToRest(method = "/api/v1/chat.update")
    void updateMessage(String msgId, String roomId, String message, SimpleCallback callback) {
        throw new UnsupportedOperationException("update message not implemented");
    }

    @MoveToRest(method = "/api/v1/{channels,groups,im}.unPinMessage")
    void unpinMessage(JSONObject message, SimpleCallback callback) {
        throw new UnsupportedOperationException("unpin message not implemented");
    }

    @MoveToRest(method = "/api/v1/chat.starMessage")
    void starMessage(String msgId, String roomId, Boolean starred, SimpleCallback callback) {
        throw new UnsupportedOperationException("star message not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    void setReaction(String emojiId, String msgId, SimpleCallback callback) {
        throw new UnsupportedOperationException("set reaction not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.messages",
            "/api/v1/groups.messages",
            "/api/v1/dm.messages",
    })
    void searchMessage(String message, String roomId, int limit,
                       SimpleListCallback<Message> callback) {
        throw new UnsupportedOperationException("search message not implemented");
    }

    @MoveToRest(method = "/api/v1/channels.create")
    public void createPublicGroup(String groupName, String[] users, Boolean readOnly,
                                  RoomCallback.GroupCreateCallback callback) {
        throw new UnsupportedOperationException("create public channel not implemented");
    }

    @MoveToRest(method = "/api/v1/groups.create")
    public void createPrivateGroup(String groupName, String[] users,
                                   RoomCallback.GroupCreateCallback callback) {
        throw new UnsupportedOperationException("create private channel not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.delete",
            "/api/v1/groups.delete",
            "/api/v1/dm.close" // couldn't find a delete version for DMs
    })
    void deleteGroup(String roomId, SimpleCallback callback) {
        throw new UnsupportedOperationException("delete channel not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.archive",
            "/api/v1/groups.archive",
            //no archive method for DMs
    })
    void archiveRoom(String roomId, SimpleCallback callback) {
        throw new UnsupportedOperationException("archive channel not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.unarchive",
            "/api/v1/groups.unarchive",
            //no unarchive method for DMs
    })
    void unarchiveRoom(String roomId, SimpleCallback callback) {
        throw new UnsupportedOperationException("unarchive channel not implemented");
    }

    @MoveToRest(method = "/api/v1/channels.join")
    public void joinPublicGroup(String roomId, String joinCode, SimpleCallback callback) {
        throw new UnsupportedOperationException("join channel not implemented");
    }

    @MoveToRest(method = "/api/v1/channels.leave")
    void leaveGroup(String roomId, SimpleCallback callback) {
        throw new UnsupportedOperationException("leave channel not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.close",
            "/api/v1/groups.close",
            "/api/v1/dm.close"
    })
    void hideRoom(String roomId, SimpleCallback callback) {
        throw new UnsupportedOperationException("hide room not implemented");
    }

    @MoveToRest(methods = {
            "/api/v1/channels.open",
            "/api/v1/groups.open",
            "/api/v1/dm.open"
    })
    void openRoom(String roomId, SimpleCallback callback) {
        throw new UnsupportedOperationException("hide room not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    void setFavouriteRoom(String roomId, Boolean isFavouriteRoom, SimpleCallback callback) {
        throw new UnsupportedOperationException("set favorite not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    void sendFileMessage(String roomId, String store, String fileId, String fileType,
                         int size, String fileName, String desc, String url,
                         MessageCallback.MessageAckCallback callback) {
        throw new UnsupportedOperationException("send file message not implemented");
    }

    @MoveToRest
    @MissingRestMethod
    public void setStatus(User.Status s, SimpleCallback callback) {
        throw new UnsupportedOperationException("set status not implemented");
    }

    public void subscribeActiveUsers(SubscribeCallback subscribeCallback) {
        websocketImpl.subscribeActiveUsers(subscribeCallback);
    }

    public void subscribeUserData(SubscribeCallback subscribeCallback) {
        websocketImpl.subscribeUserData(subscribeCallback);
    }

    public void subscribeUserRoles(SubscribeCallback subscribeCallback) {
        websocketImpl.subscribeUserRoles(subscribeCallback);
    }

    public void subscribeLoginConf(SubscribeCallback subscribeCallback) {
        websocketImpl.subscribeLoginConf(subscribeCallback);
    }

    public void subscribeClientVersions(SubscribeCallback subscribeCallback) {
        websocketImpl.subscribeClientVersions(subscribeCallback);
    }

    String subscribeRoomFiles(String roomId, int limit, SubscribeCallback listener) {
        return websocketImpl.subscribeRoomFiles(roomId, limit, listener);
    }

    String subscribeMentionedMessages(String roomId, int limit, SubscribeCallback listener) {
        return websocketImpl.subscribeMentionedMessages(roomId, limit, listener);
    }

    String subscribeStarredMessages(String roomId, int limit, SubscribeCallback listener) {
        return websocketImpl.subscribeStarredMessages(roomId, limit, listener);
    }

    String subscribePinnedMessages(String roomId, int limit, SubscribeCallback listener) {
        return websocketImpl.subscribePinnedMessages(roomId, limit, listener);
    }

    String subscribeSnipettedMessages(String roomId, int limit, SubscribeCallback listener) {
        return websocketImpl.subscribeSnipettedMessages(roomId, limit, listener);
    }

    //Tested
    String subscribeRoomMessageEvent(String roomId, Boolean enable, SubscribeCallback subscribeCallback, MessageCallback.MessageListener listener) {
        return websocketImpl.subscribeRoomMessageEvent(roomId, enable, subscribeCallback, listener);
    }

    String subscribeRoomTypingEvent(String roomId, Boolean enable, SubscribeCallback subscribeCallback, TypingListener listener) {
        return websocketImpl.subscribeRoomTypingEvent(roomId, enable, subscribeCallback, listener);
    }

    String subscribeRoomDeleteEvent(String roomId, boolean enable, SubscribeCallback subscribeCallback) {
        return websocketImpl.subscribeRoomDeleteEvent(roomId, enable, subscribeCallback);
    }

    void unsubscribeRoom(String subId, SubscribeCallback subscribeCallback) {
        websocketImpl.unsubscribeRoom(subId, subscribeCallback);
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
        private ISO8601Converter dateConverter;

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

        public Builder dateConverter(ISO8601Converter dateConverter) {
            this.dateConverter = checkNotNull(dateConverter, "dateConverter == null");
            return this;
        }

        public RocketChatClient build() {
            return new RocketChatClient(this);
        }
    }
}
