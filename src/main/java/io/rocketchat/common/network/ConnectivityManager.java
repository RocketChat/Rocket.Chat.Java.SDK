package io.rocketchat.common.network;

import io.rocketchat.common.listener.ConnectListener;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by sachin on 11/8/17.
 */
public class ConnectivityManager {

    private ConcurrentLinkedQueue<ConnectListener> listeners;


    public void register(ConnectListener listener) {
        if (listener != null) {
            if (listeners == null) {
                listeners = new ConcurrentLinkedQueue<>();
                listeners.add(listener);
            } else {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
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
