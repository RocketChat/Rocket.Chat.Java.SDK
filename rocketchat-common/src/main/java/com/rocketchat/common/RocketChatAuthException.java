package com.rocketchat.common;

public class RocketChatAuthException extends RocketChatException {

    public RocketChatAuthException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + getMessage() + '\'' +
                '}';
    }
}
