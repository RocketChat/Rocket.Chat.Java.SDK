package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.RoomObject;
import io.rocketchat.core.model.RoomRole;

import java.util.ArrayList;

/**
 * Created by sachin on 20/7/17.
 */
public class RoomListener {
    public interface GetRoomListener extends Listener {
        void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error);
    }

    public interface RoomRolesListener extends Listener{
        void onGetRoomRoles(ArrayList <RoomRole> roles, ErrorObject error);
    }
}
