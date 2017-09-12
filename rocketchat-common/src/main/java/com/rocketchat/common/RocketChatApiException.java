package com.rocketchat.common;

import org.json.JSONObject;

/**
 * Created by sachin on 12/6/17.
 */

public class RocketChatApiException extends RocketChatException {

    private String reason;
    private String errorType;
    private long error;
    private String message;

    public RocketChatApiException(JSONObject object) {
        super(object.optString("message"));
        reason = object.optString("reason");
        errorType = object.optString("errorType");
        error = object.optLong("error");
        message = object.optString("message");
    }

    public String getReason() {
        return reason;
    }

    public String getErrorType() {
        return errorType;
    }

    public long getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ErrorObject{" +
                "reason='" + reason + '\'' +
                ", errorType='" + errorType + '\'' +
                ", error=" + error +
                ", message='" + message + '\'' +
                '}';
    }
}

