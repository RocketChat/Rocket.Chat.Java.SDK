package com.rocketchat.core.middleware;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.utils.Pair;
import com.rocketchat.core.callback.*;
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
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sachin on 18/7/17.
 */

// TODO: 20/8/17 Process callbacks on UIThread and backgroundThread
public class CoreMiddleware {

    private ConcurrentHashMap<Long, Pair<Listener, ListenerType>> callbacks;

    public CoreMiddleware() {
        callbacks = new ConcurrentHashMap<>();
    }

    public void createCallback(long i, Listener listener, CoreMiddleware.ListenerType type) {
        if (listener != null) {
            callbacks.put(i, Pair.create(listener, type));
        }
    }

    public void processCallback(long i, JSONObject object) {
        if (callbacks.containsKey(i)) {
            Pair<Listener, ListenerType> listenerPair = callbacks.remove(i);
            Listener listener = listenerPair.first;
            CoreMiddleware.ListenerType type = listenerPair.second;
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    LoginListener loginListener = (LoginListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        loginListener.onLoginError(errorObject);
                    } else {
                        TokenObject tokenObject = new TokenObject((JSONObject) result);
                        loginListener.onLoginSuccess(tokenObject);
                    }
                    break;
                case GET_PERMISSIONS:
                    AccountListener.getPermissionsListener getPermissionsListener = (AccountListener.getPermissionsListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        getPermissionsListener.onGetPermissions(null, errorObject);
                    } else {
                        ArrayList<Permission> permissions = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            permissions.add(new Permission(array.optJSONObject(j)));
                        }
                        getPermissionsListener.onGetPermissions(permissions, null);
                    }
                    break;
                case GET_PUBLIC_SETTINGS:
                    AccountListener.getPublicSettingsListener getPublicSettingsListener = (AccountListener.getPublicSettingsListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        getPublicSettingsListener.onGetPublicSettings(null, errorObject);
                    } else {
                        ArrayList<PublicSetting> settings = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            settings.add(new PublicSetting(array.optJSONObject(j)));
                        }
                        getPublicSettingsListener.onGetPublicSettings(settings, null);
                    }
                    break;
                case GET_USER_ROLES:
                    UserListener.getUserRoleListener userRoleListener = (UserListener.getUserRoleListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        userRoleListener.onUserRoles(null, errorObject);
                    } else {
                        ArrayList<UserObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new UserObject(array.optJSONObject(j)));
                        }
                        userRoleListener.onUserRoles(list, null);
                    }
                    break;
                case GET_SUBSCRIPTIONS:
                    GetSubscriptionListener subscriptionListener = (GetSubscriptionListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        subscriptionListener.onGetSubscriptions(null, errorObject);
                    } else {
                        ArrayList<SubscriptionObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new SubscriptionObject(array.optJSONObject(j)));
                        }
                        subscriptionListener.onGetSubscriptions(list, null);
                    }
                    break;
                case GET_ROOMS:
                    RoomCallback.GetRoomCallback getRoomListener = (RoomCallback.GetRoomCallback) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        getRoomListener.onError(errorObject);
                    } else {
                        ArrayList<RoomObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomObject(array.optJSONObject(j)));
                        }
                        getRoomListener.onGetRooms(list);
                    }
                    break;
                case GET_ROOM_ROLES:
                    RoomCallback.RoomRolesCallback roomRolesListener = (RoomCallback.RoomRolesCallback) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        roomRolesListener.onError(errorObject);
                    } else {
                        ArrayList<RoomRole> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomRole(array.optJSONObject(j)));
                        }
                        roomRolesListener.onGetRoomRoles(list);
                    }
                    break;
                case LIST_CUSTOM_EMOJI:
                    EmojiListener emojiListener = (EmojiListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        emojiListener.onListCustomEmoji(null, errorObject);
                    } else {
                        ArrayList<Emoji> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new Emoji(array.optJSONObject(j)));
                        }
                        emojiListener.onListCustomEmoji(list, null);
                    }
                    break;
                case LOAD_HISTORY:
                    HistoryListener historyListener = (HistoryListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        historyListener.onLoadHistory(null, 0, errorObject);
                    } else {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = ((JSONObject) result).optInt("unreadNotLoaded");
                        historyListener.onLoadHistory(list, unreadNotLoaded, null);
                    }
                    break;
                case GET_ROOM_MEMBERS:
                    RoomCallback.GetMembersListener membersListener = (RoomCallback.GetMembersListener) listener;
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
                    MessageListener.MessageAckListener ackListener = (MessageListener.MessageAckListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        ackListener.onMessageAck(null, errorObject);
                    } else {
                        RocketChatMessage message = new RocketChatMessage((JSONObject) result);
                        ackListener.onMessageAck(message, null);
                    }
                    break;
                case MESSAGE_OP:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case SEARCH_MESSAGE:
                    MessageListener.SearchMessageListener searchMessageListener = (MessageListener.SearchMessageListener) listener;
                    if (result == null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        searchMessageListener.onSearchMessage(null, errorObject);
                    } else {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        searchMessageListener.onSearchMessage(list, null);
                    }
                    break;
                case CREATE_GROUP:
                    RoomCallback.GroupListener groupListener = (RoomCallback.GroupListener) listener;
                    if (object.opt("error") != null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        groupListener.onError(errorObject);
                    } else {
                        String roomId = ((JSONObject) result).optString("rid");
                        groupListener.onCreateGroup(roomId);
                    }
                    break;
                case DELETE_GROUP:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case ARCHIVE:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case UNARCHIVE:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case JOIN_PUBLIC_GROUP:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case LEAVE_GROUP:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case OPEN_ROOM:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case HIDE_ROOM:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case SET_FAVOURITE_ROOM:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case SET_STATUS:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
                case UFS_CREATE:
                    IFileUpload.UfsCreateListener ufsCreateListener = (IFileUpload.UfsCreateListener) listener;
                    if (object.opt("error") != null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        ufsCreateListener.onUfsCreate(null, errorObject);
                    } else {
                        FileUploadToken token = new FileUploadToken((JSONObject) result);
                        ufsCreateListener.onUfsCreate(token, null);
                    }
                    break;
                case UFS_COMPLETE:
                    IFileUpload.UfsCompleteListener completeListener = (IFileUpload.UfsCompleteListener) listener;
                    if (object.opt("error") != null) {
                        ApiError errorObject = new ApiError(object.optJSONObject("error"));
                        completeListener.onUfsComplete(null, errorObject);
                    } else {
                        FileObject file = new FileObject((JSONObject) result);
                        completeListener.onUfsComplete(file, null);
                    }
                    break;
                case LOGOUT:
                    handleCallbackBySimpleListener((SimpleCallback) listener, object.opt("error"));
                    break;
            }
        }
    }

    public void notifyDisconnection(String message) {

    }

    private void handleCallbackBySimpleListener(SimpleCallback listener, Object error) {
        if (error != null) {
            Error errorObject = new ApiError((JSONObject) error);
            listener.onError(errorObject);
        } else {
            listener.onSuccess();
        }
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
