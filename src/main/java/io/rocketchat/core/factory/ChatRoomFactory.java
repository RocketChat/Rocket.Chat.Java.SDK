package io.rocketchat.core.factory;

import io.rocketchat.common.data.model.Room;
import io.rocketchat.core.RocketChatAPI;

import java.util.ArrayList;

/**
 * Created by sachin on 29/7/17.
 */
public class ChatRoomFactory {

    RocketChatAPI api;
    ArrayList<RocketChatAPI.ChatRoom> rooms;

    public ChatRoomFactory(RocketChatAPI api) {
        this.api = api;
    }

    public ChatRoomFactory createChatRooms(ArrayList <? extends Room> roomObjects){
        rooms=new ArrayList<>();
        for (Room room : roomObjects){
            rooms.add(createChatRoom(room));
        }
        return this;
    }

    public RocketChatAPI.ChatRoom createChatRoom(Room room){
        return api.new ChatRoom(room);
    }


    public ArrayList <RocketChatAPI.ChatRoom> getChatRooms(){
        return rooms;
    }

    public RocketChatAPI.ChatRoom getChatRoomByName(String roomName){
        for (RocketChatAPI.ChatRoom room : rooms){
            if (room.getRoomData().getRoomName().equals(roomName)){
                return room;
            }
        }
        return null;
    }

    public RocketChatAPI.ChatRoom getChatRoomById(String roomId){
        for (RocketChatAPI.ChatRoom room : rooms){
            if (room.getRoomData().getRoomId().equals(roomId)){
                return room;
            }
        }
        return null;
    }

    public Boolean removeChatRoomByName(String roomName){
        for (RocketChatAPI.ChatRoom room : rooms){
            if (room.getRoomData().getRoomName().equals(roomName)){
                return rooms.remove(room);
            }
        }
        return false;
    }

    public Boolean removeChatRoomById(String roomId){
        for (RocketChatAPI.ChatRoom room : rooms){
            if (room.getRoomData().getRoomId().equals(roomId)){
                return rooms.remove(room);
            }
        }
        return false;
    }

}
