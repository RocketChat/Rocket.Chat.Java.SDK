package io.rocketchat.common.data.model;

import org.json.JSONObject;

/**
 * Created by sachin on 12/6/17.
 */

public class ErrorObject {

    private String reason;
    private String errorType;
    private long error;
    private String message;

    public ErrorObject(JSONObject object) {
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

    public String getMessage() {
        return message;
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

