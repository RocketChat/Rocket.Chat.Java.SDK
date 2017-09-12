package com.rocketchat.core.factory;

import com.rocketchat.common.data.model.Room;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.model.SubscriptionObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by sachin on 29/7/17.
 */

// TODO: 29/7/17 add methods for getting rooms based on favourites, one to one and groups (public and private)
// TODO: 29/7/17 might have to make separate arraylist for each type, seems little impossible, better to keep generic
public class ChatRoomFactory {

    private RocketChatAPI api;
    private ArrayList<RocketChatAPI.ChatRoom> rooms;

    public static final String FAVORITE = "f";
    public static final String DIRECT = "d";
    public static final String PUBLIC = "c";
    public static final String PRIVATE = "p";

    public ChatRoomFactory(RocketChatAPI api) {
        this.api = api;
        rooms = new ArrayList<>();
    }

    private RocketChatAPI.ChatRoom createChatRoom(Room room) {
        return api.new ChatRoom(room);
    }

    public ChatRoomFactory createChatRooms(List<? extends Room> roomObjects) {
        removeAllChatRooms();
        for (Room room : roomObjects) {
            rooms.add(createChatRoom(room));
        }
        return this;
    }

    public ChatRoomFactory addChatRoom(Room room) {
        if (getChatRoomByName(room.getRoomName()) == null) {
            RocketChatAPI.ChatRoom newRoom = createChatRoom(room);
            rooms.add(newRoom);
        }
        return this;
    }

    public ArrayList<RocketChatAPI.ChatRoom> getChatRooms() {
        return rooms;
    }


    public ArrayList<RocketChatAPI.ChatRoom> getPrivateGroups() {
        ArrayList<RocketChatAPI.ChatRoom> groups = new ArrayList<>();
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomType() == Room.Type.PRIVATE) {
                groups.add(room);
            }
        }
        return groups;
    }

    public ArrayList<RocketChatAPI.ChatRoom> getPublicGroups() {
        ArrayList<RocketChatAPI.ChatRoom> groups = new ArrayList<>();
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomType() == Room.Type.PUBLIC) {
                groups.add(room);
            }
        }
        return groups;
    }

    public ArrayList<RocketChatAPI.ChatRoom> getDirectRooms() {
        ArrayList<RocketChatAPI.ChatRoom> directRooms = new ArrayList<>();
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomType() == Room.Type.ONE_TO_ONE) {
                directRooms.add(room);
            }
        }
        return directRooms;
    }

    public ArrayList<RocketChatAPI.ChatRoom> getFavoriteRooms() {
        ArrayList<RocketChatAPI.ChatRoom> favorites = new ArrayList<>();
        for (RocketChatAPI.ChatRoom room : rooms) {
            Room roomObject = room.getRoomData();
            if (roomObject instanceof SubscriptionObject) {
                if (((SubscriptionObject) roomObject).isFavourite()) {
                    favorites.add(room);
                }
            }
        }
        return favorites;
    }

    private ArrayList<RocketChatAPI.ChatRoom> removeFavorite(ArrayList<RocketChatAPI.ChatRoom> rooms) {

        ListIterator<RocketChatAPI.ChatRoom> roomListIterator = rooms.listIterator();
        while (roomListIterator.hasNext()) {
            Room roomObject = roomListIterator.next().getRoomData();
            if (roomObject instanceof SubscriptionObject) {
                if (((SubscriptionObject) roomObject).isFavourite()) {
                    roomListIterator.remove();
                }
            }
        }
        return rooms;
    }

    /**
     * This has four types of rooms
     * Favorite room can have all types of rooms, other rooms do not contains favorites
     *
     * @return returns sorted rooms in the form of hashmap with keys
     * 1. ChatRoomFactory.FAVORITE
     * 2. ChatRoomFactory.DIRECT
     * 3. ChatRoomFactory.PUBLIC
     * 4. ChatRoomFactory.PRIVATE
     */

    public HashMap<String, ArrayList<RocketChatAPI.ChatRoom>> getSortedRooms() {
        HashMap<String, ArrayList<RocketChatAPI.ChatRoom>> rooms = new HashMap<>();
        rooms.put(FAVORITE, getFavoriteRooms());
        rooms.put(DIRECT, removeFavorite(getDirectRooms()));
        rooms.put(PUBLIC, removeFavorite(getPublicGroups()));
        rooms.put(PRIVATE, removeFavorite(getPrivateGroups()));
        return rooms;
    }

    public RocketChatAPI.ChatRoom getChatRoomByName(String roomName) {
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomName().equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    public RocketChatAPI.ChatRoom getChatRoomById(String roomId) {
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    public Boolean removeChatRoomByName(String roomName) {
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomName().equals(roomName)) {
                return rooms.remove(room);
            }
        }
        return false;
    }

    public Boolean removeChatRoomById(String roomId) {
        for (RocketChatAPI.ChatRoom room : rooms) {
            if (room.getRoomData().getRoomId().equals(roomId)) {
                return rooms.remove(room);
            }
        }
        return false;
    }

    public Boolean removeChatRoom(RocketChatAPI.ChatRoom room) {
        return rooms.remove(room);
    }

    public void removeAllChatRooms() {
        rooms.clear();
    }
}
