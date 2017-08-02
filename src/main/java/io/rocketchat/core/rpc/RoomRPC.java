package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONArray;

/**
 * Created by sachin on 24/7/17.
 */

public class RoomRPC extends RPC {

    public static String CREATEPUBLICGROUP="createChannel";
    public static String CREATEPRIVATEGROUP="createPrivateGroup";
    public static String DELETEGROUP="eraseRoom";
    public static String ARCHIVEROOM="archiveRoom";
    public static String UNARCHIEVEROOM="unarchiveRoom";
    public static String JOINPUBLICGROUP="joinRoom";
    public static String LEAVEGROUP="leaveRoom";
    public static String HIDEROOM="hideRoom";
    public static String OPENROOM="openRoom";
    public static String SETFAVOURITEROOM="toggleFavorite";
    public static String SAVEROOMSETTINGS="saveRoomSettings";

    /**
     * Creates a public channel.
     * @param groupName  name of the channel
     * @param users usernames of the people to add to the channel when it is created. This can be empty and only the caller of the method will be a member of the channel.
     * @param readOnly whether the channel is read only or not
     */
    public static String createPublicGroup(int integer, String groupName, String [] users,Boolean readOnly){
        JSONArray array= new JSONArray();
        for (int i=0;i<users.length;i++){
            array.put(users[i]);
        }
        return getRemoteMethodObject(integer,CREATEPUBLICGROUP,groupName,array,readOnly).toString();
    }

    /**
     *Creates a private group.
     * @param groupName  name of the channel
     * @param users usernames of the people to add to the private group when it is created. This can be empty and only the caller of the method will be a member of the group.
     */
    public static String createPrivateGroup(int integer,String groupName, String [] users){
        JSONArray array= new JSONArray();
        for (int i=0;i<users.length;i++){
            array.put(users[i]);
        }
        return getRemoteMethodObject(integer,CREATEPRIVATEGROUP,groupName,array).toString();
    }

    /**
     * Deleting a room, either a private group or public channel, is actually completed via the method eraseRoom. The user deleting the room must have permission to do so, by either being owner or admin.
     * @param roomId  the id of the room to delete
     */
    public static String deleteGroup(int integer, String roomId){
        return getRemoteMethodObject(integer,DELETEGROUP,roomId).toString();
    }

    /**
     * Archiving a room marks it as read only and then removes it from the channel list on the left.
     */
    public static String archieveRoom(int integer, String roomId){
        return getRemoteMethodObject(integer,ARCHIVEROOM,roomId).toString();
    }

    /**
     * Unarchiving a room removes it from being read only and then adds it back to the channel list on the left.
     */
    public static String unarchiveRoom(int integer , String roomId){
        return getRemoteMethodObject(integer,UNARCHIEVEROOM,roomId).toString();
    }


    /**
     * You can only join yourself to public channels, private groups are not joinable. Some public channels require you to enter a joinCode.
     */

    public static String joinPublicGroup(int integer, String roomId, String joinCode){
        if (joinCode==null){
            return getRemoteMethodObject(integer,JOINPUBLICGROUP,roomId).toString();
        }else{
            return getRemoteMethodObject(integer,JOINPUBLICGROUP,roomId,joinCode).toString();
        }
    }

    /**
     * You can leave any rooms, except for direct messages and except for rooms you are the last owner of.
     * @param integer
     */

    public static String leaveGroup(int integer, String roomId){
        return getRemoteMethodObject(integer,LEAVEGROUP,roomId).toString();
    }

    /**
     * When you hide a room, that room no longer shows up on the list of channels and marks the property open to false on the user’s subscription of the room.
     * @param integer
     */
    public static String hideRoom(int integer, String roomId){
        return getRemoteMethodObject(integer,HIDEROOM,roomId).toString();
    }

    /**
     * When you open a room, that room shows up on the list of channels and marks the property open to true on the user’s subscription of the room.
     * @param integer
     */
    public static String openRoom(int integer, String roomId){
        return getRemoteMethodObject(integer,OPENROOM,roomId).toString();
    }

    /**
     * When a user makes a room as a favorite, the yellow star appears and it moves the room up to the “favorites” section of the list of rooms.
     * @param integer
     */
    public static String setFavouriteRoom(int integer, String roomId, Boolean isFavourite){
        return getRemoteMethodObject(integer,SETFAVOURITEROOM,roomId,isFavourite).toString();
    }

    /**
     * Need edit room permission
     * @param integer
     */
    // TODO: 27/7/17 Need to be tested properly
    public static String saveRoomSettings(int integer,String roomId, String setting, String value){
        return getRemoteMethodObject(integer,SAVEROOMSETTINGS,roomId,setting,value).toString();
    }
}
