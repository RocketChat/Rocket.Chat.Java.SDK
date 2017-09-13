package com.rocketchat.core.model.result;

import com.rocketchat.core.model.RocketChatMessage;

import java.util.ArrayList;
import java.util.List;

public class LoadHistoryResult {
    private final List<RocketChatMessage> list;
    private final int unreadNotLoaded;

    public LoadHistoryResult(List<RocketChatMessage> list, int unreadNotLoaded) {
        this.list = list;
        this.unreadNotLoaded = unreadNotLoaded;
    }

    public List<RocketChatMessage> getList() {
        return list;
    }

    public int getUnreadNotLoaded() {
        return unreadNotLoaded;
    }
}
