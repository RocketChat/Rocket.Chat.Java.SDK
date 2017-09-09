package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.data.model.UserObject;
import com.rocketchat.common.listener.Callback;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.RoomObject;
import com.rocketchat.core.model.RoomRole;
import java.util.List;

/**
 * Created by sachin on 20/7/17.
 */
public class RoomCallback {
    public interface GroupCreateCallback extends Callback {
        void onCreateGroup(String roomId);
    }

    public interface GetMembersCallback extends Callback {
        void onGetRoomMembers(Integer total, List<UserObject> members);
    }

}
