package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.SubRPC;

/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatSubRPC extends SubRPC{


    private static String STREAMROOM="stream-room-messages";
    private static String STREAMLIVECHATROOM="stream-livechat-room";
    private static String NOTIFYROOM="stream-notify-room";
    /**
     *TESTED
     * @param uniqueid
     * @param room_id
     * @param persistenceEnable Used for adding to collections, more like using sessions for maintaining subscriptions
     * @return
     */

    public static String streamRoomMessages(String uniqueid, String room_id,Boolean persistenceEnable){
        return getRemoteSubscriptionObject(uniqueid,STREAMROOM,room_id,persistenceEnable).toString();
    }

    public static String streamLivechatRoom(String uniqueid, String room_id, Boolean persistenceEnable){
        return getRemoteSubscriptionObject(uniqueid,STREAMLIVECHATROOM,room_id,persistenceEnable).toString();
    }

    /**
     * TESTED
     * @param uniqueid
     * @param room_id
     * @param persistenceEnable
     * @return
     */
    public static String subscribeTyping(String uniqueid, String room_id, Boolean persistenceEnable){
        return getRemoteSubscriptionObject(uniqueid,NOTIFYROOM,room_id+"/typing",persistenceEnable).toString();
    }

}
