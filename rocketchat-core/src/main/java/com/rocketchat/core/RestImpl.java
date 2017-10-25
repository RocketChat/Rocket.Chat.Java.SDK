package com.rocketchat.core;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.model.BaseRoom;
import com.rocketchat.common.data.model.ServerInfo;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.PaginatedCallback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.Sort;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.ServerInfoCallback;
import com.rocketchat.core.model.Message;
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

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loginCallback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, loginCallback);
                    return;
                }

                // TODO parse message and check the response type.
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONObject data = json.getJSONObject("data");
                    String id = data.getString("userId");
                    String token = data.getString("authToken");

                    loginCallback.onLoginSuccess(new Token(id, token, null));
                } catch (JSONException e) {
                    loginCallback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
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

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, callback);
                    return;
                }

                try {
                    JsonAdapter<ServerInfo> adapter = moshi.adapter(ServerInfo.class);
                    ServerInfo info = adapter.fromJson(response.body().string());

                    callback.onServerInfo(info);
                } catch (IOException e) {
                    callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
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

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    processCallbackError(response, callback);
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    System.out.println("RESPONSE: " + json.toString());

                    callback.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // TODO
    void getRoomMembers() {

    }

    // TODO
    void getRoomFavoriteMessages() {

    }

    void getRoomPinnedMessages(String roomId,
                               BaseRoom.RoomType roomType,
                               int offset,
                               final PaginatedCallback callback) {
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
                    processCallbackError(response, callback);
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
                      final PaginatedCallback callback) {
        checkNotNull(roomId,"roomId == null");
        checkNotNull(roomType,"roomType == null");
        checkNotNull(sortBy,"sortBy == null");
        checkNotNull(sort,"sort == null");
        checkNotNull(callback,"callback == null");

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
                    processCallbackError(response, callback);
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

    /**
     * Returns the correspondent Rest API method accordingly with the room type.
     *
     * @param roomType The type of the room.
     * @param method The method.
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
     * @param method The method name.
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
            builder.addHeader("X-Auth-Token", token.getAuthToken())
                    .addHeader("X-User-Id", token.getUserId());
        }

        return builder;
    }

    private void processCallbackError(Response response, Callback callback) {
        try {
            if (response.code() == 401) {
                JSONObject json = new JSONObject(response.body().string());
                callback.onError(new RocketChatAuthException(json.optString("message")));
            } else {
                JSONObject json = new JSONObject(response.body().string());
                String message = json.optString("error");
                String errorType = json.optString("errorType");
                callback.onError(new RocketChatApiException(response.code(), message, errorType));
            }
        } catch (IOException | JSONException e) {
            callback.onError(new RocketChatException(e.getMessage(), e));
        }
    }
}