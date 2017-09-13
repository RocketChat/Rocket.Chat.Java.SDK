package com.rocketchat.core.middleware;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.*;
import com.rocketchat.core.model.result.GetRoomMembersResult;
import com.rocketchat.core.model.result.LoadHistoryResult;
import com.rocketchat.core.uploader.FileUploadToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 18/7/17.
 */

// TODO: 20/8/17 Process callbacks on UIThread and backgroundThread
public class CoreMiddleware {

    private final Map<Long, Map.Entry<ListenerType, CompletableFuture<?>>> futures = new ConcurrentHashMap<>();

    public CoreMiddleware() {
    }

    public <A> CompletableFuture<A> createCallback(long i, ListenerType type) {
        CompletableFuture<A> futureResult = new CompletableFuture<>();
        futures.put(i, new AbstractMap.SimpleEntry<>(type, futureResult));
        return futureResult;
    }

    @SuppressWarnings("unchecked")
    public void processCallback(long i, JSONObject object) {
        if (futures.containsKey(i)) {
            Map.Entry<ListenerType, CompletableFuture<?>> futureAndType = futures.remove(i);
            CoreMiddleware.ListenerType type = futureAndType.getKey();
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        TokenObject tokenObject = new TokenObject((JSONObject) result);
                        ((CompletableFuture<TokenObject>) futureAndType.getValue()).complete(tokenObject);
                    }
                    break;
                case GET_PERMISSIONS:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<Permission> permissions = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            permissions.add(new Permission(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<Permission>>) futureAndType.getValue()).complete(permissions);
                    }
                    break;
                case GET_PUBLIC_SETTINGS:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<PublicSetting> settings = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            settings.add(new PublicSetting(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<PublicSetting>>) futureAndType.getValue()).complete(settings);
                    }
                    break;
                case GET_USER_ROLES:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<UserObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new UserObject(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<UserObject>>) futureAndType.getValue()).complete(list);
                    }
                    break;
                case GET_SUBSCRIPTIONS:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<SubscriptionObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new SubscriptionObject(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<SubscriptionObject>>) futureAndType.getValue()).complete(list);
                    }
                    break;
                case GET_ROOMS:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<RoomObject> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomObject(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<RoomObject>>) futureAndType.getValue()).complete(list);
                    }
                    break;
                case GET_ROOM_ROLES:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<RoomRole> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomRole(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<RoomRole>>) futureAndType.getValue()).complete(list);
                    }
                    break;
                case LIST_CUSTOM_EMOJI:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<Emoji> list = new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new Emoji(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<Emoji>>) futureAndType.getValue()).complete(list);
                    }
                    break;
                case LOAD_HISTORY:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = ((JSONObject) result).optInt("unreadNotLoaded");
                        ((CompletableFuture<LoadHistoryResult>) futureAndType.getValue()).complete(new LoadHistoryResult(list, unreadNotLoaded));
                    }
                    break;
                case GET_ROOM_MEMBERS:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<UserObject> users = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("records");
                        for (int j = 0; j < array.length(); j++) {
                            users.add(new UserObject(array.optJSONObject(j)));
                        }
                        Integer total = ((JSONObject) result).optInt("total");
                        ((CompletableFuture<GetRoomMembersResult>) futureAndType.getValue()).complete(new GetRoomMembersResult(total, users));
                    }
                    break;
                case SEND_MESSAGE:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        RocketChatMessage message = new RocketChatMessage((JSONObject) result);
                        ((CompletableFuture<RocketChatMessage>) futureAndType.getValue()).complete(message);
                    }
                    break;
                case SEARCH_MESSAGE:
                    if (!completeExceptionallyIfResultNull(object, futureAndType.getValue(), result)) {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        ((CompletableFuture<List<RocketChatMessage>>) futureAndType.getValue()).complete(list);
                    }
                    break;
                case CREATE_GROUP:
                    if (!completeExceptionallyIfErrorNotNull(object, futureAndType.getValue())) {
                        String roomId = ((JSONObject) result).optString("rid");
                        ((CompletableFuture<String>) futureAndType.getValue()).complete(roomId);
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
                    handleCallbackBySimpleListener(futureAndType.getValue(), object.opt("error"));
                    break;
                case UFS_CREATE:
                    if (!completeExceptionallyIfErrorNotNull(object, futureAndType.getValue())) {
                        FileUploadToken token = new FileUploadToken((JSONObject) result);
                        ((CompletableFuture<FileUploadToken>) futureAndType.getValue()).complete(token);
                    }
                    break;
                case UFS_COMPLETE:
                    if (!completeExceptionallyIfErrorNotNull(object, futureAndType.getValue())) {
                        FileObject file = new FileObject((JSONObject) result);
                        ((CompletableFuture<FileObject>) futureAndType.getValue()).complete(file);
                    }
                    break;
            }
        }
    }

    private boolean completeExceptionallyIfResultNull(JSONObject object, CompletableFuture<?> futureResult, Object result) {
        if (result == null) {
            ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
            futureResult.completeExceptionally(new ErrorException(errorObject));
            return true;
        } else {
            return false;
        }
    }

    private boolean completeExceptionallyIfErrorNotNull(JSONObject object, CompletableFuture<?> futureResult) {
        if (object.opt("error") != null) {
            ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
            futureResult.completeExceptionally(new ErrorException(errorObject));
            return true;
        } else {
            return false;
        }
    }

    private void handleCallbackBySimpleListener(CompletableFuture<?> futureResult, Object error) {
        if (error != null) {
            ErrorObject errorObject = new ErrorObject((JSONObject) error);
            futureResult.completeExceptionally(new ErrorException(errorObject));
        } else {
            ((CompletableFuture<Boolean>) futureResult).complete(Boolean.TRUE);
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
