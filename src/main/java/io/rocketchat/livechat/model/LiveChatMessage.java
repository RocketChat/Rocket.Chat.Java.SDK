package io.rocketchat.livechat.model;

import org.json.JSONObject;

import io.rocketchat.common.data.model.Message;

/**
 * Created by sachin on 9/6/17.
 */
public class LiveChatMessage extends Message {

    public static String MESSAGE_TYPE_COMMAND = "command";
    public static String MESSAGE_TYPE_CLOSE = "livechat-close";

    String visitorToken;
    Boolean newRoom;
    Boolean showConnecting; //This message triggers showconnecting popup
    String sandstormSessionId;

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
