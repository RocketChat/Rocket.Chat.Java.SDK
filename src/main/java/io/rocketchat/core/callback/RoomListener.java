package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.RoomObject;

import java.util.ArrayList;

/**
 * Created by sachin on 20/7/17.
 */
public class RoomListener {
    public interface GetRoomListener extends Listener {
        void onGetRooms(ArrayList<RoomObject> rooms, ErrorObject error);
    }
}
