package com.rocketchat.common.network;

import com.rocketchat.common.listener.ConnectListener;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by sachin on 11/8/17.
 */
public class ConnectivityManager {

    private ConcurrentLinkedQueue<ConnectListener> listeners;

    ConnectivityManager() {
        listeners = new ConcurrentLinkedQueue<>();
    }

    public void register(ConnectListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void publishConnect(String sessionId) {
        for (ConnectListener listener : listeners) {
            listener.onConnect(sessionId);
        }
    }

    public void publishDisconnect(boolean closedByServer) {
        for (ConnectListener listener : listeners) {
            listener.onDisconnect(closedByServer);
        }
    }

    public void publishConnectError(Exception websocketException) {
        for (ConnectListener listener : listeners) {
            listener.onConnectError(websocketException);
        }
    }

    public Boolean unRegister(ConnectListener listener) {
        return listeners.remove(listener);
    }
}
