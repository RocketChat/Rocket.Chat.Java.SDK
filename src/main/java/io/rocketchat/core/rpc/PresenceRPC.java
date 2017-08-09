package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */
public class PresenceRPC extends RPC {

    private static final String DEFAULT_STATUS = "UserPresence:setDefaultStatus";
    private static final String TEMP_STATUS = "UserPresence:";

    public static String setDefaultStatus(int integer, Status status) {
        String defaultStat = "online";
        switch (status) {
            case ONLINE:
                defaultStat = "online";
                break;
            case BUSY:
                defaultStat = "busy";
                break;
            case AWAY:
                defaultStat = "away";
                break;
            case OFFLINE:
                defaultStat = "offline";
                break;
        }
        return getRemoteMethodObject(integer, DEFAULT_STATUS, defaultStat).toString();
    }

    public static String setTemporaryStatus(int integer, Status status) {
        String defaultStat = "online";
        if (status == Status.AWAY) {
            defaultStat = "away";
        }
        return getRemoteMethodObject(integer, TEMP_STATUS + defaultStat).toString();
    }

    public enum Status {
        ONLINE, BUSY, AWAY, OFFLINE
    }
}
