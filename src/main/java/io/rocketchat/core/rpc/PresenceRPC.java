package io.rocketchat.core.rpc;

import io.rocketchat.common.data.model.UserObject;
import io.rocketchat.common.data.model.UserObject.Status;
import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */
public class PresenceRPC extends RPC {

    private static final String DEFAULT_STATUS = "UserPresence:setDefaultStatus";
    private static final String TEMP_STATUS = "UserPresence:";

    public static String setDefaultStatus(int integer, Status status) {
        String defaultStat = UserObject.ONLINE;
        switch (status) {
            case ONLINE:
                defaultStat = UserObject.ONLINE;
                break;
            case BUSY:
                defaultStat = UserObject.BUSY;
                break;
            case AWAY:
                defaultStat = UserObject.AWAY;
                break;
            case OFFLINE:
                defaultStat = UserObject.OFFLINE;
                break;
        }
        return getRemoteMethodObject(integer, DEFAULT_STATUS, defaultStat).toString();
    }

    public static String setTemporaryStatus(int integer, Status status) {
        String tempStat = UserObject.ONLINE;
        if (status == Status.AWAY) {
            tempStat = UserObject.AWAY;
        }
        return getRemoteMethodObject(integer, TEMP_STATUS + tempStat).toString();
    }

}
