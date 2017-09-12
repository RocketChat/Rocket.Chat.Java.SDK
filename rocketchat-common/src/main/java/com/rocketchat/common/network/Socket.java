package com.rocketchat.common.network;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.StatusLine;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sachin on 7/6/17.
 */

public class Socket {

    public static final Logger LOGGER = Logger.getLogger(Socket.class.getName());

    private String url;
    private WebSocketFactory factory;
    private TaskHandler handler;
    private long pingInterval;
    private WebSocket ws;
    private WebSocketAdapter adapter;
    private ReconnectionStrategy strategy;
    private Timer timer;
    private boolean selfDisconnect;
    private boolean pingEnable;
    protected ConnectivityManager connectivityManager;

    protected Socket(String url) {
        LOGGER.setLevel(Level.INFO);
        this.url = Utils.getEndPointFromDomainName(url);
        adapter = getAdapter();
        factory = new WebSocketFactory().setConnectionTimeout(5000);
        selfDisconnect = false;
        pingEnable = false;
        pingInterval = 2000;
        handler = new TaskHandler();
        connectivityManager = new ConnectivityManager();
    }

    public void setReconnectionStrategy(ReconnectionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setPingInterval(long pingInterval) {
        pingEnable = true;
        if (pingInterval > this.pingInterval) {
            this.pingInterval = pingInterval;
        }
    }

    public void disablePing() {
        if (pingEnable) {
            handler.cancel();
            pingEnable = false;
        }
    }

    public void enablePing() {
        if (!pingEnable) {
            pingEnable = true;
            sendDataInBackground(RPC.PING_MESSAGE);
        }
    }

    public boolean isPingEnabled() {
        return pingEnable;
    }

    public State getState() {
        switch (ws.getState()) {
            case CREATED:
                return State.CREATED;
            case CONNECTING:
                return State.CONNECTING;
            case OPEN:
                return State.CONNECTED;
            case CLOSING:
                return State.DISCONNECTING;
            case CLOSED:
                return State.DISCONNECTED;
            default:
                return State.DISCONNECTED;
        }

    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    private WebSocketAdapter getAdapter() {
        return new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                Socket.this.onConnected();
                super.onConnected(websocket, headers);
            }

            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                Socket.this.onDisconnected(closedByServer);
                super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                Socket.this.onConnectError(exception);
                super.onConnectError(websocket, exception);
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                Socket.this.onTextMessage(text);
                super.onTextMessage(websocket, text);
            }
        };
    }

    /**
     * Function for connecting to server
     */

    protected void createSocket() {
        // Create a WebSocket with a socket connection timeout value.
        try {
            ws = factory.createSocket(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ws.addExtension("permessage-deflate; client_max_window_bits");
        ws.addHeader("Accept-Encoding", "gzip, deflate, sdch");
        ws.addHeader("Accept-Language", "en-US,en;q=0.8");
        ws.addHeader("Pragma", "no-cache");
        ws.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

        ws.addListener(adapter);
    }

    protected void connect() {
        try {
            // Connect to the server and perform an opening handshake.
            // This method blocks until the opening handshake is finished.
            ws.connect();
        } catch (OpeningHandshakeException e) {
            // A violation against the WebSocket protocol was detected
            // during the opening handshake.
            StatusLine sl = e.getStatusLine();
            System.out.println("=== Status Line ===");
            System.out.format("HTTP Version  = %s\n", sl.getHttpVersion());
            System.out.format("Status Code   = %d\n", sl.getStatusCode());
            System.out.format("Reason Phrase = %s\n", sl.getReasonPhrase());

            // HTTP headers.
            Map<String, List<String>> headers = e.getHeaders();
            System.out.println("=== HTTP Headers ===");
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                // Header name.
                String name = entry.getKey();

                // Values of the header.
                List<String> values = entry.getValue();

                if (values == null || values.size() == 0) {
                    // Print the name only.
                    System.out.println(name);
                    continue;
                }

                for (String value : values) {
                    // Print the name and the value.
                    System.out.format("%s: %s\n", name, value);
                }
            }
        } catch (WebSocketException e) {
            System.out.println("Got websocket exception " + e.getMessage());
            // Failed to establish a WebSocket connection.
        }
    }

    protected void connectAsync() {
        ws.connectAsynchronously();
    }

    private void sendData(String message) {
        if (getState() == State.CONNECTED) {
            ws.sendText(message);
        }
    }

    protected void sendDataInBackground(final String message) {
        EventThread.exec(new Runnable() {
            @Override
            public void run() {
                if (getState() == State.CONNECTED) {
                    ws.sendText(message);
                }
            }
        });
    }

    public void reconnect() {
        try {
            ws = ws.recreate(5000).connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        ws.disconnect();
        selfDisconnect = true;
    }

    protected void onConnected() {
        strategy.setNumberOfAttempts(0);
        LOGGER.info("Connected to server");
    }

    protected void onDisconnected(boolean closedByServer) {
        LOGGER.warning("Disconnected from server");
        processReconnection();
    }

    protected void onConnectError(Exception websocketException) {
        LOGGER.warning("Connect error");
        processReconnection();
    }

    protected void onTextMessage(String text) throws Exception {
        LOGGER.info("Message is " + text);
    }

    private void processReconnection() {
        if (strategy != null && !selfDisconnect) {
            if (strategy.getNumberOfAttempts() < strategy.getMaxAttempts()) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        reconnect();
                        strategy.processAttempts();
                        timer.cancel();
                        timer.purge();
                    }
                }, strategy.getReconnectInterval());

            } else {
                handler.cancel();
                LOGGER.info("Number of attempts are complete");
            }
        } else {
            handler.cancel();
            selfDisconnect = false;
        }
    }

    // TODO: 15/8/17 solve problem of PONG RECEIVE FAILED by giving a fair chance
    protected void sendPingFramesPeriodically() {
        handler.removeLast();
        handler.postDelayed(new TimerTask() {
            @Override
            public void run() {
                sendDataInBackground(RPC.PING_MESSAGE);
                LOGGER.info("SENDING PING");
            }
        }, pingInterval);
        handler.postDelayed(new TimerTask() {
            @Override
            public void run() {
                if (getState() != State.DISCONNECTING && getState() != State.DISCONNECTED) {
                    LOGGER.warning("PONG RECEIVE FAILED");
                    ws.disconnect(WebSocketCloseCode.NONE, "PONG RECEIVE FAILED", 0);
                }
            }
        }, 2 * pingInterval);
    }

    public enum State {
        CREATED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
    }

}

