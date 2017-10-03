package com.rocketchat.core.internal.helper;

import com.rocketchat.common.data.model.BaseRoom;

public final class RestHelper {
    public static String messageListPath(BaseRoom.RoomType type) {
        switch (type) {
            case PUBLIC:
                return "channels.messages";
            case PRIVATE:
                return "groups.messages";
            case ONE_TO_ONE:
                return "dm.messages";
        }
        throw new IllegalArgumentException("type must be PUBLIC, PRIVATE or ONE_TO_ONE");
    }
}
