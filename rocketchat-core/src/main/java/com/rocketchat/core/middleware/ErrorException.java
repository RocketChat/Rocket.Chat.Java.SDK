package com.rocketchat.core.middleware;

import com.rocketchat.common.data.model.ErrorObject;

public class ErrorException extends RuntimeException {

    private final ErrorObject errorObject;

    public ErrorException(ErrorObject errorObject) {
        this.errorObject = errorObject;
    }

    public ErrorObject getErrorObject() {
        return errorObject;
    }
}
