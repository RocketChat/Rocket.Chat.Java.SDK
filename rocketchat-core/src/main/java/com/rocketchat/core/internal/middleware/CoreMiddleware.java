package com.rocketchat.core.internal.middleware;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.model.internal.TypedListResponse;
import com.rocketchat.common.data.model.internal.TypedResponse;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.utils.Json;
import com.rocketchat.common.utils.Pair;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 18/7/17.
 */


// TODO: 9/10/17 Removed some of the APIs need to fix them, especially file uploading API's, calls for upload has been removed

public class CoreMiddleware {

    private final Moshi moshi;
    private ConcurrentHashMap<Long, Pair<? extends Callback, CallbackType>> callbacks;

    public CoreMiddleware(Moshi moshi) {
        this.moshi = moshi;
        callbacks = new ConcurrentHashMap<>();
    }

    public void createCallback(long i, Callback callback, CallbackType type) {
        callbacks.put(i, Pair.create(callback, type));
    }

    @SuppressWarnings("unchecked")
    public void processCallback(long id, JSONObject object, String message) {
        JSONArray array;
        Type type;
        if (callbacks.containsKey(id)) {
            Pair<? extends Callback, CallbackType> callbackPair = callbacks.remove(id);
            Callback callback = callbackPair.first;
            CallbackType callbackType = callbackPair.second;
            Object result = object.opt("result");

            /*
             * Possibly add a validateResponse(result, type) here or return some
             * RocketChatInvalidResponseException...
             */
            if (result == null) {
                JSONObject errorObject = object.optJSONObject("error");
                if (errorObject == null) {
                    String error = "Missing \"result\" or \"error\" values: " + object.toString();
                    callback.onError(new RocketChatInvalidResponseException(error));
                } else {
                    callback.onError(new RocketChatApiException(errorObject));
                }
                return;
            }

            try {
                switch (callbackType) {
                    case LOGIN:
                        LoginCallback loginCallback = (LoginCallback) callback;
                        type = Types.newParameterizedType(TypedResponse.class, Token.class);
                        TypedResponse<Token> response = Json.parseJson(moshi, type, message);
                        loginCallback.onLoginSuccess(response.result());
                        break;
                    case GET_PERMISSIONS:
                        SimpleListCallback<Permission> permissionCallback = (SimpleListCallback<Permission>) callback;
                        type = Types.newParameterizedType(TypedListResponse.class, Permission.class);
                        TypedListResponse<Permission> permissions = Json.parseJson(moshi, type, message);
                        permissionCallback.onSuccess(permissions.result());
                        break;
                    case GET_SUBSCRIPTIONS:
                        SimpleListCallback<Subscription> subscriptionCallback = (SimpleListCallback<Subscription>) callback;
                        type = Types.newParameterizedType(TypedListResponse.class, Subscription.class);
                        TypedListResponse<Subscription> subscriptions = Json.parseJson(moshi, type, message);
                        subscriptionCallback.onSuccess(subscriptions.result());
                        break;
                }
            } catch (JsonDataException jsonException) {
                callback.onError(new RocketChatInvalidResponseException(jsonException.getMessage(), jsonException));
            } catch (IOException e) {
                callback.onError(new RocketChatInvalidResponseException(e.getMessage(), e));
                e.printStackTrace();
            }
        }
    }

    public void notifyDisconnection(String message) {
        RocketChatException error = new RocketChatNetworkErrorException(message);
        for (Map.Entry<Long, Pair<? extends Callback, CallbackType>> entry : callbacks.entrySet()) {
            entry.getValue().first.onError(error);
        }
        cleanup();
    }

    public void cleanup() {
        callbacks.clear();
    }

    public enum CallbackType {
        LOGIN,
        GET_PERMISSIONS,
        GET_SUBSCRIPTIONS,
        LOGOUT
    }
}
