package com.rocketchat.core.middleware;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.data.model.NetworkError;
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

    public void processCallback(long i, JSONObject object) {
        if (callbacks.containsKey(i)) {
            Pair<? extends Callback, CallbackType> callbackPair = callbacks.remove(i);
            Callback callback = callbackPair.first;
            CallbackType type = callbackPair.second;
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    LoginCallback loginCallback = (LoginCallback) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        loginCallback.onError(errorObject);
                    } else {
                        TokenObject tokenObject = new TokenObject((JSONObject) result);
                        loginCallback.onLoginSuccess(tokenObject);
                    }
                    break;
                case GET_PERMISSIONS:
                    SimpleListCallback<Permission> permissionCallback = (SimpleListCallback<Permission>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        permissionCallback.onError(errorObject);
                    } else {
                        List<Permission> permissions = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            permissions.add(new Permission(array.optJSONObject(j)));
                        }
                        permissionCallback.onSuccess(permissions);
                    }
                    break;
                case GET_PUBLIC_SETTINGS:
                    SimpleListCallback<PublicSetting> settingsCallback = (SimpleListCallback<PublicSetting>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        settingsCallback.onError(errorObject);
                    } else {
                        ArrayList<PublicSetting> settings = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            settings.add(new PublicSetting(array.optJSONObject(j)));
                        }
                        settingsCallback.onSuccess(settings);
                    }
                    break;
                case GET_USER_ROLES:
                    SimpleListCallback<UserObject> rolesCallback = (SimpleListCallback<UserObject>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        rolesCallback.onError(errorObject);
                    } else {
                        ArrayList<UserObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new UserObject(array.optJSONObject(j)));
                        }
                        rolesCallback.onSuccess(list);
                    }
                    break;
                case GET_SUBSCRIPTIONS:
                    SimpleListCallback<SubscriptionObject> subscriptionCallback = (SimpleListCallback<SubscriptionObject>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        subscriptionCallback.onError(errorObject);
                    } else {
                        ArrayList<SubscriptionObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new SubscriptionObject(array.optJSONObject(j)));
                        }
                        subscriptionCallback.onSuccess(list);
                    }
                    break;
                case GET_ROOMS:
                    SimpleListCallback<RoomObject> roomCallback = (SimpleListCallback<RoomObject>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        roomCallback.onError(errorObject);
                    } else {
                        ArrayList<RoomObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomObject(array.optJSONObject(j)));
                        }
                        roomCallback.onSuccess(list);
                    }
                    break;
                case GET_ROOM_ROLES:
                    SimpleListCallback<RoomRole> roomRolesCallback = (SimpleListCallback<RoomRole>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        roomRolesCallback.onError(errorObject);
                    } else {
                        ArrayList<RoomRole> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomRole(array.optJSONObject(j)));
                        }
                        roomRolesCallback.onSuccess(list);
                    }
                    break;
                case LIST_CUSTOM_EMOJI:
                    SimpleListCallback<Emoji> emojiCallback = (SimpleListCallback<Emoji>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        emojiCallback.onError(errorObject);
                    } else {
                        ArrayList<Emoji> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new Emoji(array.optJSONObject(j)));
                        }
                        emojiCallback.onSuccess(list);
                    }
                    break;
                case LOAD_HISTORY:
                    HistoryCallback historyListener = (HistoryCallback) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        historyListener.onError(errorObject);
                    } else {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = ((JSONObject) result).optInt("unreadNotLoaded");
                        historyListener.onLoadHistory(list, unreadNotLoaded);
                    }
                    break;
                case GET_ROOM_MEMBERS:
                    RoomCallback.GetMembersCallback membersListener = (RoomCallback.GetMembersCallback) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        membersListener.onError(errorObject);
                    } else {
                        ArrayList<UserObject> users = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("records");
                        for (int j = 0; j < array.length(); j++) {
                            users.add(new UserObject(array.optJSONObject(j)));
                        }
                        Integer total = ((JSONObject) result).optInt("total");
                        membersListener.onGetRoomMembers(total, users);
                    }
                    break;
                case SEND_MESSAGE:
                    MessageCallback.MessageAckCallback ackCallback = (MessageCallback.MessageAckCallback) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        ackCallback.onError(errorObject);
                    } else {
                        RocketChatMessage message = new RocketChatMessage((JSONObject) result);
                        ackCallback.onMessageAck(message);
                    }
                    break;
                case SEARCH_MESSAGE:
                    SimpleListCallback<RocketChatMessage> searchMessageCallback = (SimpleListCallback<RocketChatMessage>) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        searchMessageCallback.onError(errorObject);
                    } else {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        searchMessageCallback.onSuccess(list);
                    }
                    break;
                case CREATE_GROUP:
                    RoomCallback.GroupCreateCallback groupListener = (RoomCallback.GroupCreateCallback) callback;
                    if (object.opt("error") != null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        groupListener.onError(errorObject);
                    } else {
                        String roomId = ((JSONObject) result).optString("rid");
                        groupListener.onCreateGroup(roomId);
                    }
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
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case UFS_CREATE:
                    IFileUpload.UfsCreateCallback ufsCreateListener = (IFileUpload.UfsCreateCallback) callback;
                    if (object.opt("error") != null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        ufsCreateListener.onError(errorObject);
                    } else {
                        FileUploadToken token = new FileUploadToken((JSONObject) result);
                        ufsCreateListener.onUfsCreate(token);
                    }
                    break;
                case UFS_COMPLETE:
                    IFileUpload.UfsCompleteListener completeListener = (IFileUpload.UfsCompleteListener) callback;
                    if (object.opt("error") != null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        completeListener.onError(errorObject);
                    } else {
                        FileObject file = new FileObject((JSONObject) result);
                        completeListener.onUfsComplete(file);
                    }
                    break;
            }
        }
    }

    private void handleCallbackBySimpleListener(SimpleCallback callback, Object error) {
        if (error != null) {
            Error errorObject = new ApiError((JSONObject) error);
            callback.onError(errorObject);
        } else {
            callback.onSuccess();
        }
    }

    public void notifyDisconnection(String message) {
        Error error = new NetworkError(message);
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

        CallbackType(Class<? extends Callback> type) {
            this.type = type;
        }

        CallbackType(Class<? extends Callback> callbackClass, Class<?>... parameter) {
            type = Types.newParameterizedType(callbackClass, parameter);
        }

        public void assertCallbackType(Callback otherType) {
            if (!Types.equals(otherType.getClassType(), type)) {
                throw new ClassCastException("Invalid callback type: " + Types.getRawType(otherType.getClass()) + ", expected callback type: " + type);
            }
        }
    }
}
