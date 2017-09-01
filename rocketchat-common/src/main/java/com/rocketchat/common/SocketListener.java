package com.rocketchat.common;

import org.json.JSONObject;

public interface SocketListener {
    void onConnected();

    void onMessageReceived(JSONObject message);

    void onClosing();

    void onClosed();

    void onFailure(Throwable throwable);
}
