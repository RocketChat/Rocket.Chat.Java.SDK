package io.rocketchat.rpc;

import io.rocketchat.Utils;

/**
 * Created by sachin on 8/6/17.
 */
public class BasicRPC {

    public static String ConnectObject(){
        return "{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}";
    }

    public static String getInitialData(int integer){
        String visitorToken= Utils.generateRandomHexToken(16);
        return "{\"msg\":\"method\"," +
                "\"method\":\"livechat:getInitialData\"," +
                "\"params\":[\""+visitorToken+"\"]," +
                "\"id\":\""+integer+"\"}\"";
    }


}
