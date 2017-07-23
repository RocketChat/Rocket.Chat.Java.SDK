package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.SubRPC;

/**
 * Created by sachin on 21/7/17.
 */
public class SubscriptionRPC extends SubRPC{
    public static String STREAMROOMMESSAGES="stream-room-messages";

    public static String subscribeRoom(String uniqueid, String room_id,Boolean persistenceEnable){
        return getRemoteSubscriptionObject(uniqueid,STREAMROOMMESSAGES,room_id,persistenceEnable).toString();
    }

}
