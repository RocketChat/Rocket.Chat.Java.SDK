package com.rocketchat.core.rpc;

import com.rocketchat.common.data.model.BaseUser;
import com.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */
public class PresenceRPC extends RPC {

    private static final String DEFAULT_STATUS = "UserPresence:setDefaultStatus";
    private static final String TEMP_STATUS = "UserPresence:";

    public static String setDefaultStatus(int integer, BaseUser.Status status) {
        String defaultStat = BaseUser.ONLINE;
        switch (status) {
            case ONLINE:
                defaultStat = BaseUser.ONLINE;
                break;
            case BUSY:
                defaultStat = BaseUser.BUSY;
                break;
            case AWAY:
                defaultStat = BaseUser.AWAY;
                break;
            case OFFLINE:
                defaultStat = BaseUser.OFFLINE;
                break;
        }
        return getRemoteMethodObject(integer, DEFAULT_STATUS, defaultStat).toString();
    }

    public static String setTemporaryStatus(int integer, BaseUser.Status status) {
        String tempStat = BaseUser.ONLINE;
        if (status == BaseUser.Status.AWAY) {
            tempStat = BaseUser.AWAY;
        }
        return getRemoteMethodObject(integer, TEMP_STATUS + tempStat).toString();
    }

}
