package com.rocketchat.common.utils;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.CommonJsonAdapterFactory;
import com.rocketchat.common.data.model.ServerInfo;
import com.rocketchat.common.listener.SimpleCallback;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import static com.rocketchat.common.utils.Preconditions.checkNotNull;

public class RocketChatVerifier {

    public static void checkServerVersion(OkHttpClient client, String baseUrl,
                                          final VersionVerifier versionVerifier,
                                          final SimpleCallback callback) {
        checkNotNull(client, "client == null");
        checkNotNull(baseUrl, "baseUrl == null");
        checkNotNull(versionVerifier, "versionVerifier == null");
        checkNotNull(callback, "callback == null");

        final Moshi moshi = new Moshi.Builder().add(CommonJsonAdapterFactory.create()).build();

        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api")
                .addPathSegment("info")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(new RocketChatNetworkErrorException("network error", e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        JSONObject json = new JSONObject(response.body().string());
                        String message = json.optString("error");
                        String errorType = json.optString("errorType");
                        callback.onError(new RocketChatApiException(response.code(), message,
                                errorType));
                    }

                    JsonAdapter<ServerInfo> adapter = moshi.adapter(ServerInfo.class);
                    ServerInfo info = adapter.fromJson(response.body().string());

                    if (versionVerifier.isValidVersion(info.version())) {
                        callback.onSuccess();
                    } else {
                        callback.onError(new RocketChatException("Unsupported version: "
                                + info.version()));
                    }
                } catch (IOException | JSONException e) {
                    callback.onError(new RocketChatException(e.getMessage(), e));
                }
            }
        });
    }

    public interface VersionVerifier {
        boolean isValidVersion(String version);
    }
}
