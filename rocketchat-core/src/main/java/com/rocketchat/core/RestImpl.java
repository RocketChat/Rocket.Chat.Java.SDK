package com.rocketchat.core;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.model.BaseRoom;
import com.rocketchat.common.data.model.BaseUser;
import com.rocketchat.common.data.model.ServerInfo;
import com.rocketchat.common.data.model.User;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.PaginatedCallback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.Sort;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.ServerInfoCallback;
import com.rocketchat.core.internal.model.RestResult;
import com.rocketchat.core.internal.model.RestToken;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.model.attachment.Attachment;
import com.rocketchat.core.provider.TokenProvider;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.rocketchat.common.utils.Preconditions.checkNotNull;

class RestImpl {

    private final OkHttpClient client;
    private final HttpUrl baseUrl;
    private final TokenProvider tokenProvider;
    private final Moshi moshi;
    private final Logger logger;

    RestImpl(OkHttpClient client, Moshi moshi, HttpUrl baseUrl, TokenProvider tokenProvider, Logger logger) {
        this.client = client;
        this.moshi = moshi;
        this.baseUrl = baseUrl;
        this.tokenProvider = tokenProvider;
        this.logger = logger;
    }

    void signin(String username, String password, final LoginCallback loginCallback) {
        checkNotNull(username, "username == null");
        checkNotNull(password, "password == null");
        checkNotNull(loginCallback, "loginCallback == null");

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        HttpUrl url = requestUrl(baseUrl, "login")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Type type = Types.newParameterizedType(RestResult.class, RestToken.class);
        handleSimpleRestCall(request, type, new ValueCallback<RestResult<RestToken>>() {
            @Override
            public void onValue(RestResult<RestToken> data) {
                RestToken restToken = data.result();
                Token token = Token.create(restToken.userId(), restToken.authToken());
                if (tokenProvider != null) {
                    tokenProvider.saveToken(token);
                }
                loginCallback.onLoginSuccess(token);
            }
        }, ERROR_HANDLER(loginCallback));
    }

    void serverInfo(final ServerInfoCallback callback) {
        checkNotNull(callback, "callback == null");

        HttpUrl url = baseUrl.newBuilder()
                .addPathSegment("api")
                .addPathSegment("info")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        handleSimpleRestCall(request, ServerInfo.class, new ValueCallback<ServerInfo>() {
            @Override
            public void onValue(ServerInfo data) {
                callback.onServerInfo(data);
            }
        }, ERROR_HANDLER(callback));
    }

    void pinMessage(String messageId, final SimpleCallback callback) {
        checkNotNull(messageId, "messageId == null");
        checkNotNull(callback, "callback == null");

        RequestBody body = new FormBody.Builder()
                .add("messageId", messageId)
                .build();

        HttpUrl httpUrl = requestUrl(baseUrl, "chat.pinMessage")
                .build();

        Request request = requestBuilder(httpUrl)
                .post(body)
                .build();

        Type type = Types.newParameterizedType(RestResult.class, Message.class);
        handleSimpleRestCall(request, type, new ValueCallback<RestResult<Message>>() {
            @Override
            public void onValue(RestResult<Message> data) {
                /* TODO - should we just ignore the message, since it comes on the message stream...
                 * Maybe we should send the message, since the user could have unsubscribed
                 * the stream before the call returns
                 */
                logger.debug("Pinned message: " + data.result());
                callback.onSuccess();
            }
        }, ERROR_HANDLER(callback));
    }

    void getRoomMembers(String roomId,
                        BaseRoom.RoomType roomType,
                        int offset,
                        BaseUser.SortBy sortBy,
                        Sort sort,
                        final PaginatedCallback<User> callback) {
        checkNotNull(roomId, "roomId == null");
        checkNotNull(roomType, "roomType == null");
        checkNotNull(sortBy, "sortBy == null");
        checkNotNull(sort, "sort == null");
        checkNotNull(callback, "callback == null");

        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(roomType, "members"))
                .addQueryParameter("roomId", roomId)
                .addQueryParameter("offset", String.valueOf(offset))
                // TODO add the sort on the query parameter. Track the status here: 
                //.addQueryParameter("sort", "{\"" + sortBy.getPropertyName() + "\":" + sort.getDirection() + "}")
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("Network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    logger.info("Response = " + response.body().string());
                    processCallbackError(response, ERROR_HANDLER(callback));
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    logger.info("Response = " + json.toString());

                    Type type = Types.newParameterizedType(List.class, User.class);
                    JsonAdapter<List<User>> adapter = moshi.adapter(type);
                    List<User> userList = adapter.fromJson(json.getJSONArray("members").toString());

                    callback.onSuccess(userList, json.optInt("total"));
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    void getRoomFavoriteMessages(String roomId,
                                 BaseRoom.RoomType roomType,
                                 int offset,
                                 final PaginatedCallback<Message> callback) {
        String userId = tokenProvider.getToken().userId();
        checkNotNull(userId, "userId == null");
        checkNotNull(roomId, "roomId == null");
        checkNotNull(roomType, "roomType == null");
        checkNotNull(callback, "callback == null");

        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
                .addQueryParameter("roomId", roomId)
                .addQueryParameter("offset", String.valueOf(offset))
                .addQueryParameter("query", "{\"starred._id\":{\"$in\":[\"" + userId + "\"]}}")
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, ERROR_HANDLER(callback));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    logger.info("Response = " + json.toString());

                    Type type = Types.newParameterizedType(List.class, Message.class);
                    JsonAdapter<List<Message>> adapter = moshi.adapter(type);
                    List<Message> messageList = adapter.fromJson(json.getJSONArray("messages").toString());

                    callback.onSuccess(messageList, json.optInt("total"));
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    /**
     * Lists all of the private groups the calling user has joined.
     */
    void getUserGroupList(final SimpleListCallback<Subscription> callback) {
        checkNotNull(callback, "callback == null");

        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(BaseRoom.RoomType.PRIVATE, "list"))
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response,ERROR_HANDLER(callback));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());

                    Type type = Types.newParameterizedType(List.class, Subscription.class);
                    JsonAdapter<List<Subscription>> adapter = moshi.adapter(type);
                    List<Subscription> subscriptionList = adapter.fromJson(json.getJSONArray("groups").toString());

                    callback.onSuccess(subscriptionList);
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    /**
     * Lists all of the channels the calling user has joined.
     */
    void getUserChannelList(final SimpleListCallback<Subscription> callback) {
        checkNotNull(callback, "callback == null");

        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(BaseRoom.RoomType.PUBLIC, "list.joined"))
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, ERROR_HANDLER(callback));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());

                    Type type = Types.newParameterizedType(List.class, Subscription.class);
                    JsonAdapter<List<Subscription>> adapter = moshi.adapter(type);
                    List<Subscription> subscriptionList = adapter.fromJson(json.getJSONArray("channels").toString());

                    callback.onSuccess(subscriptionList);
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    /**
     * Lists all of the direct messages the calling user has joined.
     */
    void getUserDirectMessageList(final SimpleListCallback<Subscription> callback) {
        checkNotNull(callback, "callback == null");

        // TODO check if the REST api call is ok because we are calling /api/v1/dm.list instead of /api/v1/im.list
        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(BaseRoom.RoomType.ONE_TO_ONE, "list"))
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, ERROR_HANDLER(callback));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());

                    Type type = Types.newParameterizedType(List.class, Subscription.class);
                    JsonAdapter<List<Subscription>> adapter = moshi.adapter(type);
                    List<Subscription> subscriptionList = adapter.fromJson(json.getJSONArray("ims").toString());

                    callback.onSuccess(subscriptionList);
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    void getRoomPinnedMessages(String roomId,
                               BaseRoom.RoomType roomType,
                               int offset,
                               final PaginatedCallback<Message> callback) {
        checkNotNull(roomId,"roomId == null");
        checkNotNull(roomType,"roomType == null");
        checkNotNull(callback,"callback == null");

        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(roomType, "messages"))
                .addQueryParameter("roomId", roomId)
                .addQueryParameter("offset", String.valueOf(offset))
                .addQueryParameter("query", "{\"pinned\":true}")
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, ERROR_HANDLER(callback));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    logger.info("Response = " + json.toString());

                    Type type = Types.newParameterizedType(List.class, Message.class);
                    JsonAdapter<List<Message>> adapter = moshi.adapter(type);
                    List<Message> messageList = adapter.fromJson(json.getJSONArray("messages").toString());

                    callback.onSuccess(messageList, json.optInt("total"));
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    void getRoomFiles(String roomId,
                      BaseRoom.RoomType roomType,
                      int offset,
                      Attachment.SortBy sortBy,
                      Sort sort,
                      final PaginatedCallback<Attachment> callback) {
        checkNotNull(roomId, "roomId == null");
        checkNotNull(roomType, "roomType == null");
        checkNotNull(sortBy, "sortBy == null");
        checkNotNull(sort, "sort == null");
        checkNotNull(callback, "callback == null");

        HttpUrl httpUrl = requestUrl(baseUrl, getRestApiMethodNameByRoomType(roomType, "files"))
                .addQueryParameter("roomId", roomId)
                .addQueryParameter("offset", String.valueOf(offset))
                .addQueryParameter("sort", "{\"" + sortBy.getPropertyName() + "\":" + sort.getDirection() + "}")
                .build();

        Request request = requestBuilder(httpUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, ERROR_HANDLER(callback));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    logger.info("Response = " + json.toString());

                    JSONArray filesJSONArray = json.getJSONArray("files");
                    int length = filesJSONArray.length();
                    List<Attachment> attachments = new ArrayList<>(length);
                    for (int i = 0; i < length; ++i) {
                        attachments.add(new Attachment(filesJSONArray.getJSONObject(i), baseUrl.url().toString()));
                    }

                    callback.onSuccess(attachments, json.optInt("total"));
                } catch (JSONException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });

    }

    private interface ValueCallback<T> {
        void onValue(T data);
    }

    private interface ErrorCallback {
        void onError(RocketChatException error);
    }

    private static ErrorCallback ERROR_HANDLER(final Callback callback) {
        return new ErrorCallback() {
            @Override
            public void onError(RocketChatException error) {
                callback.onError(error);
            }
        };
    }

    private <T> void handleSimpleRestCall(Request request,
                                          final Type type,
                                          final ValueCallback<T> valueCallback,
                                          final ErrorCallback errorCallback) {
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                errorCallback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, errorCallback);
                    return;
                }

                try {
                    JsonAdapter<T> adapter = moshi.adapter(type);
                    T data = adapter.fromJson(response.body().source());
                    valueCallback.onValue(data);
                } catch (IOException e) {
                    errorCallback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    /**
     * Returns the correspondent Rest API method accordingly with the room type.
     *
     * @param roomType The type of the room.
     * @param method   The method.
     * @return A Rest API method accordingly with the room type.
     * @see #requestUrl(HttpUrl, String)
     */
    private String getRestApiMethodNameByRoomType(BaseRoom.RoomType roomType, String method) {
        switch (roomType) {
            case PUBLIC:
                return "channels." + method;
            case PRIVATE:
                return "groups." + method;
            default:
                return "dm." + method;
        }
    }

    /**
     * Builds and returns the HttpUrl.Builder as {baseUrl}/api/v1/{method}
     *
     * @param baseUrl The base URL.
     * @param method  The method name.
     * @return A HttpUrl pointing to the REST API call.
     */
    private HttpUrl.Builder requestUrl(HttpUrl baseUrl, String method) {
        return baseUrl.newBuilder()
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment(method);
    }

    /**
     * Builds and returns the Request.Builder with HttpUrl and header.
     * Note: The user token and its ID will be added to the header only if present.
     *
     * @param httpUrl The HttpUrl.
     * @return A Request.Builder with HttpUrl and the user token and its ID on the header (only if the tokenProvider is present).
     */
    private Request.Builder requestBuilder(HttpUrl httpUrl) {
        Request.Builder builder = new Request.Builder()
                .url(httpUrl);

        if (tokenProvider != null && tokenProvider.getToken() != null) {
            Token token = tokenProvider.getToken();
            builder.addHeader("X-Auth-Token", token.authToken())
                    .addHeader("X-User-Id", token.userId());
        }

        return builder;
    }

    private void processCallbackError(Response response, ErrorCallback callback) {
        try {
            String body = response.body().string();
            logger.debug("Error body: %s", body);
            if (response.code() == 401) {
                JSONObject json = new JSONObject(body);
                callback.onError(new RocketChatAuthException(json.optString("message")));
            } else {
                JSONObject json = new JSONObject(body);
                String message = json.optString("error");
                String errorType = json.optString("errorType");
                callback.onError(new RocketChatApiException(response.code(), message, errorType));
            }
        } catch (IOException | JSONException e) {
            callback.onError(new RocketChatException(e.getMessage(), e));
        }
    }
}