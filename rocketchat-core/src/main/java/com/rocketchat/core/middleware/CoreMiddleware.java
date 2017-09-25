package com.rocketchat.core.middleware;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.model.User;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.utils.Pair;
import com.rocketchat.common.utils.Types;
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
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONException;
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

// TODO: 20/8/17 Process callbacks on UIThread and backgroundThread
public class CoreMiddleware {

    private final Moshi moshi;
    private ConcurrentHashMap<Long, Pair<? extends Callback, CallbackType>> callbacks;

    public CoreMiddleware(Moshi moshi) {
        this.moshi = moshi;
        callbacks = new ConcurrentHashMap<>();
    }

    public void createCallback(long i, Callback callback, CallbackType type) {
        type.assertCallbackType(callback);
        callbacks.put(i, Pair.create(callback, type));
    }

    @SuppressWarnings("unchecked")
    public void processCallback(long i, JSONObject object) {
        JSONArray array;
        if (callbacks.containsKey(i)) {
            Pair<? extends Callback, CallbackType> callbackPair = callbacks.remove(i);
            Callback callback = callbackPair.first;
            CallbackType type = callbackPair.second;
            Object result = object.opt("result");

            /*
             * Possibly add a validateResponse(result, type) here or return some
             * RocketChatInvalidResponseException...
             */
            if (result == null) {
                callback.onError(new RocketChatApiException(object.optJSONObject("error")));
                return;
            }

            try {
                switch (type) {
                    case LOGIN:
                        LoginCallback loginCallback = (LoginCallback) callback;
                        Token tokenObject = new Token((JSONObject) result);
                        loginCallback.onLoginSuccess(tokenObject);
                        break;
                    case GET_PERMISSIONS:
                        SimpleListCallback<Permission> permissionCallback = (SimpleListCallback<Permission>) callback;
                        array = (JSONArray) result;
                        List<Permission> permissions = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            permissions.add(new Permission(array.optJSONObject(j)));
                        }
                        permissionCallback.onSuccess(permissions);
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
                        Message message = getMessageAdapter().fromJson(result.toString());
                        ackCallback.onMessageAck(message);
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
            } catch (JSONException exception) {
                callback.onError(new RocketChatInvalidResponseException(exception.getMessage(), exception));
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
        LOGIN(LoginCallback.class),
        GET_PERMISSIONS(SimpleListCallback.class, Permission.class),
        GET_PUBLIC_SETTINGS(SimpleListCallback.class, PublicSetting.class),
        GET_USER_ROLES(SimpleListCallback.class, User.class),
        GET_SUBSCRIPTIONS(SimpleListCallback.class, Subscription.class),
        GET_ROOMS(SimpleListCallback.class, Room.class),
        GET_ROOM_ROLES(SimpleListCallback.class, RoomRole.class),
        LIST_CUSTOM_EMOJI(SimpleListCallback.class, Emoji.class),
        LOAD_HISTORY(HistoryCallback.class),
        GET_ROOM_MEMBERS(RoomCallback.GetMembersCallback.class),
        SEND_MESSAGE(MessageCallback.MessageAckCallback.class),
        MESSAGE_OP(SimpleCallback.class),
        SEARCH_MESSAGE(SimpleListCallback.class, Message.class),
        CREATE_GROUP(RoomCallback.GroupCreateCallback.class),
        DELETE_GROUP(SimpleCallback.class),
        ARCHIVE(SimpleCallback.class),
        UNARCHIVE(SimpleCallback.class),
        JOIN_PUBLIC_GROUP(SimpleCallback.class),
        LEAVE_GROUP(SimpleCallback.class),
        OPEN_ROOM(SimpleCallback.class),
        HIDE_ROOM(SimpleCallback.class),
        SET_FAVOURITE_ROOM(SimpleCallback.class),
        SET_STATUS(SimpleCallback.class),
        UFS_CREATE(IFileUpload.UfsCreateCallback.class),
        UFS_COMPLETE(IFileUpload.UfsCompleteListener.class),
        LOGOUT(SimpleCallback.class);

        private Type type;

        CallbackType(Class<? extends Callback> callbackClass, Class<?>... parameters) {
            if (parameters == null || parameters.length == 0) {
                type = callbackClass;
            } else {
                type = Types.newParameterizedType(callbackClass, parameters);
            }
        }

        public void assertCallbackType(Callback otherType) {
            if (!Types.equals(otherType.getClassType(), type)) {
                throw new ClassCastException("Invalid callback type: " + otherType.getClass() + ", expected callback type: " + type);
            }
        }
    }
}
