package com.rocketchat.livechat.model;

import com.rocketchat.common.data.model.Message;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */
public class LiveChatMessage extends Message {

    public static String MESSAGE_TYPE_COMMAND = "command";
    public static String MESSAGE_TYPE_CLOSE = "livechat-close";

    private String visitorToken;
    private Boolean newRoom;
    private Boolean showConnecting; //This message triggers showconnecting popup
    private String sandstormSessionId;

    public LiveChatMessage(JSONObject object) {
        super(object);
        visitorToken = object.optString("token");
        newRoom = object.optBoolean("newRoom");
        showConnecting = object.optBoolean("showConnecting");
        sandstormSessionId = object.optString("sandstormSessionId");

    }

    public String getVisitorToken() {
        return visitorToken;
    }

    public Boolean getNewRoom() {
        return newRoom;
    }

    public Boolean getShowConnecting() {
        return showConnecting;
    }

    public String getSandstormSessionId() {
        return sandstormSessionId;
    }

}
