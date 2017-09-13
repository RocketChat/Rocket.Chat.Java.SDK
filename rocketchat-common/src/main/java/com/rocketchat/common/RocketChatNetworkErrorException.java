package com.rocketchat.common;

public class RocketChatNetworkErrorException extends RocketChatException {

    public RocketChatNetworkErrorException(String message) {
        super(message);
    }

    public RocketChatNetworkErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + getMessage() + '\'' +
                '}';
    }
}
