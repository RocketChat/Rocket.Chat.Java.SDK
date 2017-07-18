package io.rocketchat.livechat.model;

import io.rocketchat.common.data.model.Message;
import org.json.JSONObject;

/**
 * Created by sachin on 9/6/17.
 */
public class MessageObject extends Message{

    public static String MESSAGE_TYPE_COMMAND="command";
    public static String MESSAGE_TYPE_CLOSE="livechat-close";

    String visitorToken;
    String senderAlias;
    Boolean newRoom;
    Boolean showConnecting; //This message triggers showconnecting popup
    String sandstormSessionId;
    String messagetype;

    public MessageObject(JSONObject object){
        super(object);
            visitorToken=object.optString("token");
            senderAlias=object.optString("alias");
            newRoom=object.optBoolean("newRoom");
            showConnecting=object.optBoolean("showConnecting");
            sandstormSessionId=object.optString("sandstormSessionId");
            messagetype=object.optString("t");
    }

    public String getVisitorToken() {
        return visitorToken;
    }

    public String getSenderAlias() {
        return senderAlias;
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

    public String getMessagetype() {
        return messagetype;
    }
}
