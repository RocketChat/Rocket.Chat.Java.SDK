package io.rocketchat.livechat.rpc;

import io.rocketchat.common.data.rpc.RPC;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
/**
 * Created by sachin on 9/6/17.
 */

public class LiveChatHistoryRPC extends RPC{


    public static String HISTORY="loadHistory";

    /**
     * @// TODO: 15/6/17 Need to check history along with pagination support
     * HALF TESTED
     * @param integer
     * @param roomId
     * @param oldestMessageTimestamp Used to do pagination (null means latest timestamp)
     * @param count The message quantity, messages are loaded having timestamp older than @param oldestMessageTimestamp
     * @param lastTimestamp Date of the last time when client got data (Used to calculate unread)[unread count suggests number of unread messages having timestamp above @param lastTimestamp]
     * @return
     */

    public static String loadHistory(int integer, String roomId, Date oldestMessageTimestamp,Integer count, Date lastTimestamp){
        JSONObject oldestTs=null;
        JSONObject lastTs = null;
        try {
            if (oldestMessageTimestamp!=null){
                oldestTs=new JSONObject();
                oldestTs.put("$date",((int) oldestMessageTimestamp.getTime() / 1000));
            }
            if (lastTimestamp!=null){
                lastTs = new JSONObject();
                lastTs.put("$date", ((int) lastTimestamp.getTime() / 1000));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteMethodObject(integer,HISTORY,roomId,oldestTs,count,lastTs).toString();
    }
}
