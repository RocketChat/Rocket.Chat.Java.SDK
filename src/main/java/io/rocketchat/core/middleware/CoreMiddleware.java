package io.rocketchat.core.middleware;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.core.callback.AccountListener;
import io.rocketchat.core.callback.EmojiListener;
import io.rocketchat.core.callback.HistoryListener;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.callback.MessageListener;
import io.rocketchat.core.callback.RoomListener;
import io.rocketchat.core.callback.SubscriptionListener;
import io.rocketchat.core.callback.UserListener;
import io.rocketchat.core.model.Emoji;
import io.rocketchat.core.model.Permission;
import io.rocketchat.core.model.PublicSetting;
import io.rocketchat.core.model.RocketChatMessage;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.RoomRole;
import io.rocketchat.core.model.SubscriptionObject;
import io.rocketchat.core.model.TokenObject;

/**
 * Created by sachin on 18/7/17.
 */

public class CoreMiddleware {

    private static CoreMiddleware middleware = new CoreMiddleware();
    private ConcurrentHashMap<Long, Object[]> callbacks;

    private CoreMiddleware() {
        callbacks = new ConcurrentHashMap<>();
    }

    public static CoreMiddleware getInstance() {
        return middleware;
    }

    public void createCallback(long i, Listener listener, CoreMiddleware.ListenerType type) {
        if (listener != null) {
            callbacks.put(i, new Object[]{listener, type});
        }
    }

    public void processCallback(long i, JSONObject object) {
        if (callbacks.containsKey(i)) {
            Object[] objects = callbacks.remove(i);
            Listener listener = (Listener) objects[0];
            CoreMiddleware.ListenerType type = (CoreMiddleware.ListenerType) objects[1];
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    LoginListener loginListener = (LoginListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        loginListener.onLogin(null, errorObject);
                    } else {
                        TokenObject tokenObject = new TokenObject((JSONObject) result);
                        loginListener.onLogin(tokenObject, null);
                    }
                    break;
                case GET_PERMISSIONS:
                    AccountListener.getPermissionsListener getPermissionsListener = (AccountListener.getPermissionsListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
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
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
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
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
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
                    SubscriptionListener.GetSubscriptionListener subscriptionListener = (SubscriptionListener.GetSubscriptionListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
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
                    RoomListener.GetRoomListener getRoomListener = (RoomListener.GetRoomListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        getRoomListener.onGetRooms(null, errorObject);
                    } else {
                        ArrayList<RoomObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomObject(array.optJSONObject(j)));
                        }
                        getRoomListener.onGetRooms(list, null);
                    }
                    break;
                case GET_ROOM_ROLES:
                    RoomListener.RoomRolesListener roomRolesListener = (RoomListener.RoomRolesListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        roomRolesListener.onGetRoomRoles(null, errorObject);
                    } else {
                        ArrayList<RoomRole> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomRole(array.optJSONObject(j)));
                        }
                        roomRolesListener.onGetRoomRoles(list, null);
                    }
                    break;
                case LIST_CUSTOM_EMOJI:
                    EmojiListener emojiListener = (EmojiListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
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
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
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
                    RoomListener.GetMembersListener membersListener= (RoomListener.GetMembersListener) listener;
                    if (result==null){
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        membersListener.onGetRoomMembers(null,null,errorObject);
                    }else {
                        ArrayList <UserObject> users=new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("records");
                        for (int j = 0; j < array.length(); j++) {
                            users.add(new UserObject(array.optJSONObject(j)));
                        }
                        Integer total = ((JSONObject) result).optInt("total");
                        membersListener.onGetRoomMembers(total,users,null);
                    }
                    break;
                case SEND_MESSAGE:
                    MessageListener.MessageAckListener ackListener = (MessageListener.MessageAckListener) listener;
                    if (result == null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        ackListener.onMessageAck(null, errorObject);
                    } else {
                        RocketChatMessage message = new RocketChatMessage((JSONObject) result);
                        ackListener.onMessageAck(message, null);
                    }
                    break;
                case MESSAGE_OP:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case CREATE_GROUP:
                    RoomListener.GroupListener groupListener = (RoomListener.GroupListener) listener;
                    if (object.opt("error") != null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        groupListener.onCreateGroup(null, errorObject);
                    } else {
                        String roomId = ((JSONObject) result).optString("rid");
                        groupListener.onCreateGroup(roomId, null);
                    }
                    break;
                case DELETE_GROUP:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case ARCHIEVE:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case UNARCHIEVE:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case JOIN_PUBLIC_GROUP:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case LEAVE_GROUP:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case OPEN_ROOM:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case HIDE_ROOM:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case SET_FAVOURITE_ROOM:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
                case LOGOUT:
                    handleCallbackBySimpleListener((SimpleListener) listener, object.opt("error"));
                    break;
            }
        }
    }

    private void handleCallbackBySimpleListener(SimpleListener listener, Object error) {
        if (error != null) {
            ErrorObject errorObject = new ErrorObject((JSONObject) error);
            listener.callback(null, errorObject);
        } else {
            listener.callback(true, null);
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
        CREATE_GROUP,
        DELETE_GROUP,
        ARCHIEVE,
        UNARCHIEVE,
        JOIN_PUBLIC_GROUP,
        LEAVE_GROUP,
        OPEN_ROOM,
        HIDE_ROOM,
        SET_FAVOURITE_ROOM,
        SET_STATUS,
        LOGOUT
    }
}
