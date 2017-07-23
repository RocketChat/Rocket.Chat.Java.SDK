package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 24/7/17.
 */

// TODO: 24/7/17 Implement everything related to channels
public class ChannelRPC extends RPC {

    /**
     * Its a creating channel
     * @return
     */
    public static String createPublicGroup(int integer){
        return "";
    }

    public static String createPrivateGroup(int integer){
        return "";
    }

    public static String deleteGroup(int integer){
        return "";
    }

    /**
     * Archving a room marks it as read only and then removes it from the channel list on the left.
     * @param integer
     * @return
     */
    public static String archieveRoom(int integer){
        return "";
    }

    /**
     * Unarchving a room removes it from being read only and then adds it back to the channel list on the left.
     * @return
     */
    public static String unarchiveRoom(int integer){
        return "";
    }


    /**
     * You can only join yourself to public channels, private groups are not joinable. Some public channels require you to enter a joinCode.
     * @return
     */
    public static String joinPublicGroup(int integer){
        return "";
    }

    /**
     * You can leave any rooms, except for direct messages and except for rooms you are the last owner of.
     * @param integer
     * @return
     */
    public static String leaveGroup(int integer){
        return "";
    }

    /**
     * When you hide a room, that room no longer shows up on the list of channels and marks the property open to false on the user’s subscription of the room.
     * @param integer
     * @return
     */
    public static String hideRoom(int integer){
        return "";
    }

    /**
     * When you open a room, that room shows up on the list of channels and marks the property open to true on the user’s subscription of the room.
     * @param integer
     * @return
     */
    public static String openRoom(int integer){
        return "";
    }

    /**
     * When a user makes a room as a favorite, the yellow star appears and it moves the room up to the “favorites” section of the list of rooms.
     * @param integer
     * @return
     */
    public static String setFavouriteRoom(int integer){
        return "";
    }

    /**
     * Need edit room permission
     * @param integer
     * @return
     */
    public static String saveRoomSettings(int integer){
        return "";
    }
}
