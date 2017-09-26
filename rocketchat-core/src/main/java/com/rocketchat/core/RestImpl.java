package com.rocketchat.core;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.provider.TokenProvider;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    RestImpl(OkHttpClient client, Moshi moshi, HttpUrl baseUrl, TokenProvider tokenProvider, Logger logger) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.tokenProvider = tokenProvider;
    }

    void signin(String username, String password, final LoginCallback loginCallback) {
        checkNotNull(username, "username == null");
        checkNotNull(password, "password == null");
        checkNotNull(loginCallback, "loginCallback == null");

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(baseUrl.newBuilder().addPathSegment("login").build())
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
                    procressCallbackError(response, loginCallback);
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
                    e.printStackTrace();
                    loginCallback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                }
            }
        });
    }

    void pinMessage(String messageId, final SimpleCallback callback) {
        RequestBody body = new FormBody.Builder()
                .add("id", messageId)
                .build();
        Request request = requestBuilder("chat.pinMessage").post(body).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    procressCallbackError(response, callback);
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

    private Request.Builder requestBuilder(String path) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl.newBuilder().addPathSegment(path).build());

        if (tokenProvider != null && tokenProvider.getToken() != null) {
            Token token = tokenProvider.getToken();
            builder.addHeader("X-Auth-Token", token.getAuthToken())
                    .addHeader("X-User-Id", token.getUserId());
        }

        return builder;
    }

    private void procressCallbackError(Response response, Callback callback) {
        // TODO - parse response body
        if (response.code() == 401) {
            callback.onError(new RocketChatAuthException("Invalid credentials"));
        } else {
            try {
                JSONObject json = new JSONObject(response.body().string());
                String message = json.optString("error");
                String errorType = json.optString("errorType");
                callback.onError(new RocketChatApiException(response.code(), message, errorType));
            } catch (IOException | JSONException e) {
                callback.onError(new RocketChatException(e.getMessage(), e));
            }
        }
    }
}
