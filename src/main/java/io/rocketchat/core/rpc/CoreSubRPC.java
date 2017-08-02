package io.rocketchat.core.rpc;

import io.rocketchat.common.data.rpc.SubRPC;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreSubRPC extends SubRPC{
    public static String STREAMROOMMESSAGES="stream-room-messages";
    private static String STREAMNOTIFYROOM ="stream-notify-room";

    public static String subscribeRoomMessageEvent(String uniqueid, String room_id,Boolean persistenceEnable){
        return getRemoteSubscriptionObject(uniqueid,STREAMROOMMESSAGES,room_id,persistenceEnable).toString();
    }

    public static String subscribeRoomTypingEvent(String uniqueid, String room_id, Boolean persistenceEnable){
        return getRemoteSubscriptionObject(uniqueid, STREAMNOTIFYROOM,room_id+"/typing",persistenceEnable).toString();
    }

    public static String unsubscribeRoom(String subId){
        return getRemoteUnsubscriptionObject(subId).toString();
    }

}
