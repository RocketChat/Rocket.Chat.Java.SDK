package com.rocketchat.common;

public class RocketChatNetworkErrorException extends RocketChatException {

    public RocketChatNetworkErrorException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + getMessage() + '\'' +
                '}';
    }
}
