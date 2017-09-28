package com.rocketchat.core.internal.rpc;

import com.rocketchat.common.data.rpc.RPC;
import org.json.JSONArray;

/**
 * Created by sachin on 24/7/17.
 */

public class RoomRPC extends RPC {

    private static final String CREATE_PUBLIC_GROUP = "createChannel";
    private static final String CREATE_PRIVATE_GROUP = "createPrivateGroup";
    private static final String DELETE_GROUP = "eraseRoom";
    private static final String ARCHIVE_ROOM = "archiveRoom";
    private static final String UNARCHIVE_ROOM = "unarchiveRoom";
    private static final String JOIN_PUBLIC_GROUP = "joinRoom";
    private static final String LEAVE_GROUP = "leaveRoom";
    private static final String HIDE_ROOM = "hideRoom";
    private static final String OPEN_ROOM = "openRoom";
    private static final String SET_FAVOURITE_ROOM = "toggleFavorite";
    private static final String SAVE_ROOM_SETTINGS = "saveRoomSettings";
    private static final String GET_ROOM_MEMBERS = "getUsersOfRoom";

    /**
     * Creates a public channel.
     *
     * @param groupName name of the channel
     * @param users     usernames of the people to add to the channel when it is created. This can be empty and only
     *                  the
     *                  caller of the method will be a member of the channel.
     * @param readOnly  whether the channel is read only or not
     */
    public static String createPublicGroup(int integer, String groupName, String[] users, Boolean readOnly) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < users.length; i++) {
            array.put(users[i]);
        }
        return getRemoteMethodObject(integer, CREATE_PUBLIC_GROUP, groupName, array, readOnly).toString();
    }

    /**
     * Creates a private group.
     *
     * @param groupName name of the channel
     * @param users     usernames of the people to add to the private group when it is created. This can be empty and
     *                  only the caller of the method will be a member of the group.
     */
    public static String createPrivateGroup(int integer, String groupName, String[] users) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < users.length; i++) {
            array.put(users[i]);
        }
        return getRemoteMethodObject(integer, CREATE_PRIVATE_GROUP, groupName, array).toString();
    }

    /**
     * Deleting a room, either a private group or public channel, is actually completed via the method eraseRoom. The
     * user deleting the room must have permission to do so, by either being owner or admin.
     *
     * @param roomId the id of the room to delete
     */
    public static String deleteGroup(int integer, String roomId) {
        return getRemoteMethodObject(integer, DELETE_GROUP, roomId).toString();
    }

    /**
     * Archiving a room marks it as read only and then removes it from the channel list on the left.
     */
    public static String archieveRoom(int integer, String roomId) {
        return getRemoteMethodObject(integer, ARCHIVE_ROOM, roomId).toString();
    }

    /**
     * Unarchiving a room removes it from being read only and then adds it back to the channel list on the left.
     */
    public static String unarchiveRoom(int integer, String roomId) {
        return getRemoteMethodObject(integer, UNARCHIVE_ROOM, roomId).toString();
    }

    /**
     * You can only join yourself to public channels, private groups are not joinable. Some public channels require you
     * to enter a joinCode.
     */

    public static String joinPublicGroup(int integer, String roomId, String joinCode) {
        if (joinCode == null) {
            return getRemoteMethodObject(integer, JOIN_PUBLIC_GROUP, roomId).toString();
        } else {
            return getRemoteMethodObject(integer, JOIN_PUBLIC_GROUP, roomId, joinCode).toString();
        }
    }

    /**
     * You can leave any rooms, except for direct messages and except for rooms you are the last owner of.
     */

    public static String leaveGroup(int integer, String roomId) {
        return getRemoteMethodObject(integer, LEAVE_GROUP, roomId).toString();
    }

    /**
     * When you hide a room, that room no longer shows up on the list of channels and marks the property open to false
     * on the user’s subscription of the room.
     */
    public static String hideRoom(int integer, String roomId) {
        return getRemoteMethodObject(integer, HIDE_ROOM, roomId).toString();
    }

    /**
     * When you open a room, that room shows up on the list of channels and marks the property open to true on the
     * user’s subscription of the room.
     */
    public static String openRoom(int integer, String roomId) {
        return getRemoteMethodObject(integer, OPEN_ROOM, roomId).toString();
    }

    /**
     * When a user makes a room as a favorite, the yellow star appears and it moves the room up to the “favorites”
     * section of the list of rooms.
     */
    public static String setFavouriteRoom(int integer, String roomId, Boolean isFavourite) {
        return getRemoteMethodObject(integer, SET_FAVOURITE_ROOM, roomId, isFavourite).toString();
    }

    /**
     * Need edit room permission
     */
    // TODO: 27/7/17 Need to be tested properly
    public static String saveRoomSettings(int integer, String roomId, String setting, String value) {
        return getRemoteMethodObject(integer, SAVE_ROOM_SETTINGS, roomId, setting, value).toString();
    }

    public static String getRoomMembers(int integer, String roomId, Boolean allUsers) {
        return getRemoteMethodObject(integer, GET_ROOM_MEMBERS, roomId, allUsers).toString();
    }


}
