package io.rocketchat.core.rpc;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.rocketchat.common.data.rpc.RPC;

/**
 * Created by sachin on 21/7/17.
 */
public class ChatHistoryRPC extends RPC {
    private static String HISTORY = "loadHistory";

    /**
     * TODO: 18/6/17 look for unread messages or new messages
     * HALF TESTED
     *
     * @param integer
     * @param roomId
     * @param oldestMessageTimestamp Used to do pagination (null means latest timestamp)
     * @param count                  The message quantity, messages are loaded having timestamp older than @param oldestMessageTimestamp
     * @param lastTimestamp          Date of the last time when client got data (Used to calculate unread)[unread count suggests number of unread messages having timestamp above @param lastTimestamp]
     * @return
     */

    public static String loadHistory(int integer, String roomId, Date oldestMessageTimestamp, Integer count, Date lastTimestamp) {
        JSONObject oldestTs = null;
        JSONObject lastTs = null;
        try {
            if (oldestMessageTimestamp != null) {
                oldestTs = new JSONObject();
                oldestTs.put("$date", oldestMessageTimestamp.getTime());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (lastTimestamp != null) {
            lastTs = new JSONObject();
            try {
                lastTs.put("$date", lastTimestamp.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return getRemoteMethodObject(integer, HISTORY, roomId, oldestTs, count, lastTs).toString();
    }
}
