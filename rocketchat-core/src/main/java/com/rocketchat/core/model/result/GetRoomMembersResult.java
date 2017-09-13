package com.rocketchat.core.model.result;

import com.rocketchat.common.data.model.UserObject;

import java.util.ArrayList;
import java.util.List;

public class GetRoomMembersResult {
    private final Integer total;
    private final List<UserObject> users;

    public GetRoomMembersResult(Integer total, List<UserObject> users) {
        this.total = total;
        this.users = users;
    }

    public Integer getTotal() {
        return total;
    }

    public List<UserObject> getUsers() {
        return users;
    }
}
