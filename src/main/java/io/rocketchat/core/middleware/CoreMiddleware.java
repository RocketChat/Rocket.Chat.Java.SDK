package io.rocketchat.core.middleware;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.common.listener.SimpleListener;
import io.rocketchat.core.callback.*;
import io.rocketchat.core.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 18/7/17.
 */

public class CoreMiddleware {


    public enum ListenerType {
        LOGIN,
        GETPERMISSIONS,
        GETPUBLICSETTINGS,
        GETUSERROLES,
        GETSUBSCRIPTIONS,
        GETROOMS,
        GETROOMROLES,
        LISTCUSTOMEMOJI,
        LOADHISTORY,
        SENDMESSAGE,
        MESSAGEOP,
        CREATEGROUP,
        DELETEGROUP,
        LOGOUT
    }

    ConcurrentHashMap<Long,Object[]> callbacks;

    public static CoreMiddleware middleware=new CoreMiddleware();

    private CoreMiddleware(){
        callbacks= new ConcurrentHashMap<>();
    }

    public static CoreMiddleware getInstance(){
        return middleware;
    }

    public void createCallback(long i, Listener listener, CoreMiddleware.ListenerType type){
        if (listener!=null) {
            callbacks.put(i, new Object[]{listener, type});
        }
    }

    public void processCallback(long i, JSONObject object){
        if (callbacks.containsKey(i)) {
            Object[] objects = callbacks.remove(i);
            Listener listener = (Listener) objects[0];
            CoreMiddleware.ListenerType type = (CoreMiddleware.ListenerType) objects[1];
            Object result = object.opt("result");
            switch (type) {
                case LOGIN:
                    LoginListener loginListener= (LoginListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        loginListener.onLogin(null,errorObject);
                    }else{
                        TokenObject tokenObject=new TokenObject((JSONObject) result);
                        loginListener.onLogin(tokenObject,null);
                    }
                    break;
                case GETPERMISSIONS:
                    AccountListener.getPermissionsListener getPermissionsListener= (AccountListener.getPermissionsListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        getPermissionsListener.onGetPermissions(null, errorObject);
                    }else{
                        ArrayList <Permission> permissions= new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            permissions.add(new Permission(array.optJSONObject(j)));
                        }
                        getPermissionsListener.onGetPermissions(permissions,null);
                    }
                    break;
                case GETPUBLICSETTINGS:
                    AccountListener.getPublicSettingsListener getPublicSettingsListener= (AccountListener.getPublicSettingsListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        getPublicSettingsListener.onGetPublicSettings(null,errorObject);
                    }else{
                        ArrayList <PublicSetting> settings= new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            settings.add(new PublicSetting(array.optJSONObject(j)));
                        }
                        getPublicSettingsListener.onGetPublicSettings(settings,null);
                    }
                    break;
                case GETUSERROLES:
                    UserListener.getUserRoleListener userRoleListener= (UserListener.getUserRoleListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        userRoleListener.onUserRoles(null,errorObject);
                    }else{
                        ArrayList<UserObject> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new UserObject(array.optJSONObject(j)));
                        }
                        userRoleListener.onUserRoles(list,null);
                    }
                    break;
                case GETSUBSCRIPTIONS:
                    SubscriptionListener.GetSubscriptionListener subscriptionListener= (SubscriptionListener.GetSubscriptionListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        subscriptionListener.onGetSubscriptions(null,errorObject);
                    }else{
                        ArrayList<SubscriptionObject> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new SubscriptionObject(array.optJSONObject(j)));
                        }
                        subscriptionListener.onGetSubscriptions(list,null);
                    }
                    break;
                case GETROOMS:
                    RoomListener.GetRoomListener getRoomListener= (RoomListener.GetRoomListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        getRoomListener.onGetRooms(null,errorObject);
                    }else{
                        ArrayList<RoomObject> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomObject(array.optJSONObject(j)));
                        }
                        getRoomListener.onGetRooms(list,null);
                    }
                    break;
                case GETROOMROLES:
                    RoomListener.RoomRolesListener roomRolesListener= (RoomListener.RoomRolesListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        roomRolesListener.onGetRoomRoles(null,errorObject);
                    }else{
                        ArrayList<RoomRole> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RoomRole(array.optJSONObject(j)));
                        }
                        roomRolesListener.onGetRoomRoles(list,null);
                    }
                    break;
                case LISTCUSTOMEMOJI:
                    EmojiListener emojiListener= (EmojiListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        emojiListener.onListCustomEmoji(null,errorObject);
                    }else{
                        ArrayList<Emoji> list=new ArrayList<>();
                        JSONArray array = (JSONArray) result;
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new Emoji(array.optJSONObject(j)));
                        }
                        emojiListener.onListCustomEmoji(list,null);
                    }
                    break;
                case LOADHISTORY:
                    HistoryListener historyListener = (HistoryListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        historyListener.onLoadHistory(null, 0,errorObject);
                    }else {
                        ArrayList<RocketChatMessage> list = new ArrayList<>();
                        JSONArray array = ((JSONObject) result).optJSONArray("messages");
                        for (int j = 0; j < array.length(); j++) {
                            list.add(new RocketChatMessage(array.optJSONObject(j)));
                        }
                        int unreadNotLoaded = ((JSONObject)result).optInt("unreadNotLoaded");
                        historyListener.onLoadHistory(list, unreadNotLoaded, null);
                        break;
                    }
                case SENDMESSAGE:
                    MessageListener.MessageAckListener ackListener= (MessageListener.MessageAckListener) listener;
                    if (result==null){
                        ErrorObject errorObject=new ErrorObject(object.optJSONObject("error"));
                        ackListener.onMessageAck(null,errorObject);
                    }else{
                        RocketChatMessage message=new RocketChatMessage((JSONObject) result);
                        ackListener.onMessageAck(message,null);
                    }
                    break;
                case MESSAGEOP:
                    handleCallbackBySimpleListener((SimpleListener) listener,object.opt("error"));
                    break;
                case CREATEGROUP:
                    RoomListener.GroupListener groupListener = (RoomListener.GroupListener) listener;
                    if (object.opt("error")!=null) {
                        ErrorObject errorObject = new ErrorObject(object.optJSONObject("error"));
                        groupListener.onCreateGroup(null,errorObject);
                    }else{
                        String roomId=((JSONObject)result).optString("rid");
                        groupListener.onCreateGroup(roomId,null);
                    }
                    break;
                case DELETEGROUP:
                    handleCallbackBySimpleListener((SimpleListener) listener,object.opt("error"));
                    break;
                case LOGOUT:
                    handleCallbackBySimpleListener((SimpleListener) listener,object.opt("error"));
                    break;
            }
        }
    }

    public void handleCallbackBySimpleListener(SimpleListener listener, Object error){
        if (error!=null){
            ErrorObject errorObject=new ErrorObject((JSONObject) error);
            listener.callback(null,errorObject);
        }else {
            listener.callback(true, null);
        }
    }
}
