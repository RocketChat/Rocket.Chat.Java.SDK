package com.rocketchat.core.rxjava;

public enum Status {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    AUTHENTICATED,
    DISCONNECTING,
    CONNECT_ERROR,
    WAITING_NETWORK,
    WAITING_TO_CONNECT
}
