package com.rocketchat.core.factory;

import com.rocketchat.common.data.model.Room;
import com.rocketchat.core.RocketChatAPI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sachin on 29/7/17.
 */

// TODO: 29/7/17 add methods for getting rooms based on favourites, one to one and groups (public and private)
// TODO: 29/7/17 might have to make separate arraylist for each type, seems little impossible, better to keep generic
public class ChatRoomFactory {

    private RocketChatAPI api;
    private ArrayList<RocketChatAPI.ChatRoom> rooms;

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
