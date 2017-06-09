package io.rocketchat.livechat.rpc;

import java.util.Date;

/**
 * Created by sachin on 9/6/17.
 */
public class LiveChatHistoryRPC {

    public static String loadHistory(int integer, String roomId, Integer count, Date lastTimestamp){
//        return "{\n" +
//                "    \"msg\": \"method\",\n" +
//                "    \"method\": \"loadHistory\",\n" +
//                "    \"id\": \""+integer+"\",\n" +
//                "    \"params\": [ \""+roomId+"\", { \"$date\": "+((int)lastTimestamp.getTime()/1000)+"} ,"+count+",null]\n" +
//                "}";
        return "{\n" +
                "    \"msg\": \"method\",\n" +
                "    \"method\": \"loadHistory\",\n" +
                "    \"id\": \""+integer+"\",\n" +
                "    \"params\": [ \""+roomId+"\", null, "+count+", { \"$date\": "+((int)lastTimestamp.getTime()/1000)+" } ]\n" +
                "}";
    }
}
