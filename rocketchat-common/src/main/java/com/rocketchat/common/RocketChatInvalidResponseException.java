package com.rocketchat.common;

public class RocketChatInvalidResponseException extends RocketChatException {
    public RocketChatInvalidResponseException(String message) {
        super(message);
    }

    public RocketChatInvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
