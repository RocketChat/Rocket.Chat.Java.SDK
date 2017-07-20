package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.model.RoomObject;

import java.util.ArrayList;

/**
 * Created by sachin on 20/7/17.
 */
public class RoomListener {
    public interface GetRoomListener{
        void onGetRooms(ArrayList<RoomObject> subscriptions, ErrorObject error);
    }
}
