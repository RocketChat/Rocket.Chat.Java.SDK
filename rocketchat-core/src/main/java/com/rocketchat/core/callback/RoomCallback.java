package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.User;
import com.rocketchat.common.listener.Callback;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by sachin on 20/7/17.
 */
public class RoomCallback {
    public static abstract class GroupCreateCallback extends Callback {
        public abstract void onCreateGroup(String roomId);

        @Override
        public Type getClassType() {
            return GroupCreateCallback.class;
        }
    }

    public static abstract class GetMembersCallback extends Callback {
        public abstract void onGetRoomMembers(Integer total, List<User> members);

        @Override
        public Type getClassType() {
            return GetMembersCallback.class;
        }
    }

}
