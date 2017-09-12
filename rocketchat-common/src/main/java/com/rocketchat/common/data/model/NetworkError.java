package com.rocketchat.common.data.model;

public class NetworkError extends Error {

    private final String message;

    public NetworkError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + message + '\'' +
                '}';
    }
}
