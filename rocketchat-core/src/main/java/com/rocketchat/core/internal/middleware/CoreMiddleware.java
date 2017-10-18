package com.rocketchat.core.internal.middleware;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.model.User;
import com.rocketchat.common.data.model.internal.TypedListResponse;
import com.rocketchat.common.data.model.internal.TypedResponse;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.utils.Json;
import com.rocketchat.common.utils.Pair;
import com.rocketchat.core.callback.HistoryCallback;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.callback.MessageCallback;
import com.rocketchat.core.callback.RoomCallback;
import com.rocketchat.core.model.Emoji;
import com.rocketchat.core.model.FileDescriptor;
import com.rocketchat.core.model.Message;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.PublicSetting;
import com.rocketchat.core.model.Room;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.Subscription;
import com.rocketchat.core.model.Token;
import com.rocketchat.core.uploader.FileUploadToken;
import com.rocketchat.core.uploader.IFileUpload;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
                    callback.onError(new RocketChatApiException(errorObject.optJSONObject("error")));
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
                    case GET_PUBLIC_SETTINGS:
                        SimpleListCallback<PublicSetting> settingsCallback = (SimpleListCallback<PublicSetting>) callback;
                        array = (JSONArray) result;
                        List<PublicSetting> settings = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            settings.add(new PublicSetting(array.optJSONObject(j)));
                        }
                        settingsCallback.onSuccess(settings);
                        break;
                    case GET_USER_ROLES:
                        SimpleListCallback<User> rolesCallback = (SimpleListCallback<User>) callback;
                        array = (JSONArray) result;
                        List<User> userObjects = getUserListAdapter().fromJson(array.toString());
                        rolesCallback.onSuccess(userObjects);
                        break;
                    case GET_SUBSCRIPTIONS:
                        SimpleListCallback<Subscription> subscriptionCallback = (SimpleListCallback<Subscription>) callback;
                        array = (JSONArray) result;
                        List<Subscription> subscriptions = getSubscriptionListAdapter().fromJson(array.toString());
                        subscriptionCallback.onSuccess(subscriptions);
                        break;
                    case GET_ROOMS:
                        SimpleListCallback<Room> roomCallback = (SimpleListCallback<Room>) callback;
                        array = (JSONArray) result;
                        List<Room> rooms = getRoomListAdapter().fromJson(array.toString());
                        roomCallback.onSuccess(rooms);
                        break;
                    case GET_ROOM_ROLES:
                        SimpleListCallback<RoomRole> roomRolesCallback = (SimpleListCallback<RoomRole>) callback;
                        array = (JSONArray) result;
                        List<RoomRole> roomRoles = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            roomRoles.add(new RoomRole(array.optJSONObject(j)));
                        }
                        roomRolesCallback.onSuccess(roomRoles);
                        break;
                    case LIST_CUSTOM_EMOJI:
                        SimpleListCallback<Emoji> emojiCallback = (SimpleListCallback<Emoji>) callback;
                        array = (JSONArray) result;
                        List<Emoji> emojis = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            emojis.add(new Emoji(array.optJSONObject(j)));
                        }
                        emojiCallback.onSuccess(emojis);
                        break;
                    case LOAD_HISTORY:
                        HistoryCallback historyCallback = (HistoryCallback) callback;
                        array = ((JSONObject) result).optJSONArray("messages");
                        List<Message> messages = getMessageListAdapter()
                                .fromJson(array.toString());
                        int unreadNotLoaded = ((JSONObject) result).optInt("unreadNotLoaded");
                        historyCallback.onLoadHistory(messages, unreadNotLoaded);
                        break;
                    case GET_ROOM_MEMBERS:
                        RoomCallback.GetMembersCallback membersCallback = (RoomCallback.GetMembersCallback) callback;
                        array = ((JSONObject) result).optJSONArray("records");
                        Integer total = ((JSONObject) result).optInt("total");
                        List<User> users = getUserListAdapter().fromJson(array.toString());
                        membersCallback.onGetRoomMembers(total, users);
                        break;
                    case SEND_MESSAGE:
                        MessageCallback.MessageAckCallback ackCallback = (MessageCallback.MessageAckCallback) callback;
                        Message msg = getMessageAdapter().fromJson(result.toString());
                        ackCallback.onMessageAck(msg);
                        break;
                    case SEARCH_MESSAGE:
                        SimpleListCallback<Message> searchMessageCallback = (SimpleListCallback<Message>) callback;
                        array = ((JSONObject) result).optJSONArray("messages");
                        List<Message> searchMessages = getMessageListAdapter().fromJson(array.toString());
                        searchMessageCallback.onSuccess(searchMessages);
                        break;
                    case CREATE_GROUP:
                        RoomCallback.GroupCreateCallback createCallback = (RoomCallback.GroupCreateCallback) callback;
                        String roomId = ((JSONObject) result).optString("rid");
                        createCallback.onCreateGroup(roomId);
                        break;
                    case UFS_CREATE:
                        IFileUpload.UfsCreateCallback ufsCreateCallback = (IFileUpload.UfsCreateCallback) callback;
                        FileUploadToken token = new FileUploadToken((JSONObject) result);
                        ufsCreateCallback.onUfsCreate(token);
                        break;
                    case UFS_COMPLETE:
                        IFileUpload.UfsCompleteListener completeCallback = (IFileUpload.UfsCompleteListener) callback;
                        FileDescriptor file = new FileDescriptor((JSONObject) result);
                        completeCallback.onUfsComplete(file);
                        break;
                    case MESSAGE_OP:
                    case DELETE_GROUP:
                    case ARCHIVE:
                    case UNARCHIVE:
                    case JOIN_PUBLIC_GROUP:
                    case LEAVE_GROUP:
                    case OPEN_ROOM:
                    case HIDE_ROOM:
                    case SET_FAVOURITE_ROOM:
                    case SET_STATUS:
                    case LOGOUT:
                        ((SimpleCallback) callback).onSuccess();
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

    private JsonAdapter<Message> messageAdapter;
    private JsonAdapter<List<Message>> messageListAdapter;
    private JsonAdapter<User> userAdapter;
    private JsonAdapter<List<User>> userListAdapter;
    private JsonAdapter<Room> roomAdapter;
    private JsonAdapter<List<Room>> roomListAdapter;
    private JsonAdapter<List<Subscription>> subscriptionListAdapter;

    private JsonAdapter<Message> getMessageAdapter() {
        if (messageAdapter == null) {
            messageAdapter = moshi.adapter(Message.class);
        }
        return messageAdapter;
    }

    private JsonAdapter<List<Message>> getMessageListAdapter() {
        if (messageListAdapter == null) {
            Type type = Types.newParameterizedType(List.class, Message.class);
            messageListAdapter = moshi.adapter(type);
        }
        return messageListAdapter;
    }

    private JsonAdapter<User> getUserAdapter() {
        if (userAdapter == null) {
            userAdapter = moshi.adapter(User.class);
        }
        return userAdapter;
    }

    private JsonAdapter<List<User>> getUserListAdapter() {
        if (userListAdapter == null) {
            Type type = Types.newParameterizedType(List.class, User.class);
            userListAdapter = moshi.adapter(type);
        }
        return userListAdapter;
    }

    public JsonAdapter<Room> getRoomAdapter() {
        if (roomAdapter == null) {
            roomAdapter = moshi.adapter(Room.class);
        }
        return roomAdapter;
    }

    public JsonAdapter<List<Room>> getRoomListAdapter() {
        if (roomListAdapter == null) {
            Type type = Types.newParameterizedType(List.class, Room.class);
            roomListAdapter = moshi.adapter(type);
        }
        return roomListAdapter;
    }

    public JsonAdapter<List<Subscription>> getSubscriptionListAdapter() {
        if (subscriptionListAdapter == null) {
            Type type = Types.newParameterizedType(List.class, Subscription.class);
            subscriptionListAdapter = moshi.adapter(type);
        }
        return subscriptionListAdapter;
    }

    public enum CallbackType {
        LOGIN,
        GET_PERMISSIONS,
        GET_PUBLIC_SETTINGS,
        GET_USER_ROLES,
        GET_SUBSCRIPTIONS,
        GET_ROOMS,
        GET_ROOM_ROLES,
        LIST_CUSTOM_EMOJI,
        LOAD_HISTORY,
        GET_ROOM_MEMBERS,
        SEND_MESSAGE,
        MESSAGE_OP,
        SEARCH_MESSAGE,
        CREATE_GROUP,
        DELETE_GROUP,
        ARCHIVE,
        UNARCHIVE,
        JOIN_PUBLIC_GROUP,
        LEAVE_GROUP,
        OPEN_ROOM,
        HIDE_ROOM,
        SET_FAVOURITE_ROOM,
        SET_STATUS,
        UFS_CREATE,
        UFS_COMPLETE,
        LOGOUT
    }
}
