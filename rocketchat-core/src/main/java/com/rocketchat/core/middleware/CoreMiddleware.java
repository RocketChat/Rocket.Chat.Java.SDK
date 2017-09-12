package com.rocketchat.core.middleware;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.data.model.UserObject;
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
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.Permission;
import com.rocketchat.core.model.PublicSetting;
import com.rocketchat.core.model.RocketChatMessage;
import com.rocketchat.core.model.RoomObject;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.SubscriptionObject;
import com.rocketchat.core.model.TokenObject;
import com.rocketchat.core.uploader.FileUploadToken;
import com.rocketchat.core.uploader.IFileUpload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private ConcurrentHashMap<Long, Pair<? extends Callback, CallbackType>> callbacks;

    public CoreMiddleware() {
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
                        TokenObject tokenObject = new TokenObject((JSONObject) result);
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
                        SimpleListCallback<UserObject> rolesCallback = (SimpleListCallback<UserObject>) callback;
                        array = (JSONArray) result;
                        List<UserObject> userObjects = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            userObjects.add(new UserObject(array.optJSONObject(j)));
                        }
                        rolesCallback.onSuccess(userObjects);
                        break;
                    case GET_SUBSCRIPTIONS:
                        SimpleListCallback<SubscriptionObject> subscriptionCallback = (SimpleListCallback<SubscriptionObject>) callback;
                        array = (JSONArray) result;
                        List<SubscriptionObject> subscriptions = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            subscriptions.add(new SubscriptionObject(array.optJSONObject(j)));
                        }
                        subscriptionCallback.onSuccess(subscriptions);
                        break;
                    case GET_ROOMS:
                        SimpleListCallback<RoomObject> roomCallback = (SimpleListCallback<RoomObject>) callback;
                        array = (JSONArray) result;
                        List<RoomObject> rooms = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            rooms.add(new RoomObject(array.optJSONObject(j)));
                        }
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
                        List<RocketChatMessage> messages = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            messages.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = ((JSONObject) result).optInt("unreadNotLoaded");
                        historyCallback.onLoadHistory(messages, unreadNotLoaded);
                        break;
                    case GET_ROOM_MEMBERS:
                        RoomCallback.GetMembersCallback membersCallback = (RoomCallback.GetMembersCallback) callback;
                        array = ((JSONObject) result).optJSONArray("records");
                        List<UserObject> users = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            users.add(new UserObject(array.optJSONObject(j)));
                        }
                        Integer total = ((JSONObject) result).optInt("total");
                        membersCallback.onGetRoomMembers(total, users);
                        break;
                    case SEND_MESSAGE:
                        MessageCallback.MessageAckCallback ackCallback = (MessageCallback.MessageAckCallback) callback;
                        RocketChatMessage message = new RocketChatMessage((JSONObject) result);
                        ackCallback.onMessageAck(message);
                        break;
                    case SEARCH_MESSAGE:
                        SimpleListCallback<RocketChatMessage> searchMessageCallback = (SimpleListCallback<RocketChatMessage>) callback;
                        array = ((JSONObject) result).optJSONArray("messages");
                        List<RocketChatMessage> searchMessages = new ArrayList<>(array.length());
                        for (int j = 0; j < array.length(); j++) {
                            searchMessages.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
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
                        FileObject file = new FileObject((JSONObject) result);
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
        LOGIN(LoginCallback.class),
        GET_PERMISSIONS(SimpleListCallback.class, Permission.class),
        GET_PUBLIC_SETTINGS(SimpleListCallback.class, PublicSetting.class),
        GET_USER_ROLES(SimpleListCallback.class, UserObject.class),
        GET_SUBSCRIPTIONS(SimpleListCallback.class, SubscriptionObject.class),
        GET_ROOMS(SimpleListCallback.class, RoomObject.class),
        GET_ROOM_ROLES(SimpleListCallback.class, RoomRole.class),
        LIST_CUSTOM_EMOJI(SimpleListCallback.class, Emoji.class),
        LOAD_HISTORY(HistoryCallback.class),
        GET_ROOM_MEMBERS(RoomCallback.GetMembersCallback.class),
        SEND_MESSAGE(MessageCallback.MessageAckCallback.class),
        MESSAGE_OP(SimpleCallback.class),
        SEARCH_MESSAGE(SimpleListCallback.class, RocketChatMessage.class),
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
