package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RoomObject;
import com.rocketchat.core.model.RoomRole;

import java.util.List;

/**
 * Created by sachin on 20/7/17.
 */
public class RoomListener {
    public interface GetRoomListener extends Listener {
        void onGetRooms(List<RoomObject> rooms, ErrorObject error);
    }

    public interface RoomRolesListener extends Listener {
        void onGetRoomRoles(List<RoomRole> roles, ErrorObject error);
    }

    public interface GroupListener extends Listener {
        void onCreateGroup(String roomId, ErrorObject error);
    }

    public interface GetMembersListener extends Listener {
        void onGetRoomMembers(Integer total, List<UserObject> members, ErrorObject error);
    }

}
