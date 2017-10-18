package com.rocketchat.common;

import com.rocketchat.common.data.model.MessageType;
import com.rocketchat.common.data.model.internal.SocketMessage;

public interface SocketListener {
    void onConnected();

    void onMessageReceived(MessageType type, String id, String message);

    void onClosing();

    void onClosed();

    void onFailure(Throwable throwable);
}
