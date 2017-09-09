package com.rocketchat.core.middleware;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.common.utils.Pair;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 18/7/17.
 */

// TODO: 20/8/17 Process callbacks on UIThread and backgroundThread
public class CoreMiddleware {

    private ConcurrentHashMap<Long, Pair<? extends Callback, ListenerType>> callbacks;

    public CoreMiddleware() {
        callbacks = new ConcurrentHashMap<>();
    }

    public void createCallback(long i, Callback listener, CoreMiddleware.ListenerType type) {
        if (listener != null) {
            callbacks.put(i, Pair.create(listener, type));
        }
    }

    public void processCallback(long i, JSONObject object) {
        if (callbacks.containsKey(i)) {
            Pair<? extends Callback, ListenerType> listenerPair = callbacks.remove(i);
            Callback callback = listenerPair.first;
            CoreMiddleware.ListenerType type = listenerPair.second;
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    LoginCallback loginListener = (LoginCallback) callback;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        loginListener.onError(errorObject);
                    } else {
                        TokenObject tokenObject = new TokenObject((JSONObject) result);
                        loginListener.onLoginSuccess(tokenObject);
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
                case MESSAGE_OP:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
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
                case DELETE_GROUP:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case ARCHIVE:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case UNARCHIVE:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case JOIN_PUBLIC_GROUP:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case LEAVE_GROUP:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case OPEN_ROOM:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case HIDE_ROOM:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case SET_FAVOURITE_ROOM:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
                case SET_STATUS:
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
                case LOGOUT:
                    handleCallbackBySimpleListener((SimpleCallback) callback, object.opt("error"));
                    break;
            }
        }
    }

    public void notifyDisconnection(String message) {

    }

    private void handleCallbackBySimpleListener(SimpleCallback callback, Object error) {
        if (error != null) {
            Error errorObject = new ApiError((JSONObject) error);
            callback.onError(errorObject);
        } else {
            callback.onSuccess();
        }
    }

    private <T> void handleSimpleListCallback(SimpleListCallback<T> callback, List<T> result) {
        callback.onSuccess(result);
    }

    public enum ListenerType {
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
