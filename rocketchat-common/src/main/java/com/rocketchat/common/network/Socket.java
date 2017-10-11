package com.rocketchat.common.network;

import com.rocketchat.common.SocketListener;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.NoopLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by sachin on 7/6/17.
 */

public /*final*/ class Socket extends WebSocketListener {

    private final SocketListener listener;
    private final Logger logger;
    private Request request;
    private OkHttpClient client;
    private String url;
    private TaskHandler pingHandler;
    private TaskHandler timeoutHandler;
    private long pingInterval;
    private WebSocket ws;
    private State currentState = State.DISCONNECTED;

    private ReconnectionStrategy strategy;
    private Timer timer;
    private boolean selfDisconnect;
    private boolean pingEnable;

    public Socket(OkHttpClient client, String url, Logger logger, SocketListener socketListener) {
        this.url = url;
        this.client = client;
        this.logger = logger;
        this.listener = socketListener;

        setState(State.DISCONNECTED);
        selfDisconnect = false;
        pingEnable = false;
        pingInterval = 2000;
        pingHandler = new TaskHandler();
        timeoutHandler = new TaskHandler();
        createSocket();
    }

    public Socket(String url, SocketListener listener) {
        this(new OkHttpClient(), url, new NoopLogger(), listener);
    }

    public void setReconnectionStrategy(ReconnectionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setPingInterval(long pingInterval) {
        pingEnable = true;
        if (pingInterval != this.pingInterval) {
            this.pingInterval = pingInterval;
        }
    }

    public void disablePing() {
        if (pingEnable) {
            pingHandler.cancel();
            pingEnable = false;
        }
    }

    public void enablePing() {
        if (!pingEnable) {
            pingEnable = true;
            sendData(RPC.PING_MESSAGE);
        }
    }

    public boolean isPingEnabled() {
        return pingEnable;
    }

    private void setState(State state) {
        logger.info(String.format("setState: old %s, new %s", currentState.name(), state.name()));
        currentState = state;
    }

    public State getState() {
        return currentState;
    }

    // OkHttp WebSocket callbacks
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        logger.info("Connected to server");
        setState(State.CONNECTED);

        if (strategy != null) {
            strategy.setNumberOfAttempts(0);
        }
        listener.onConnected();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        onTextMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        onTextMessage(bytes.toString());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        logger.info("WebSocket closing: " + code + " - " + reason);
        setState(State.DISCONNECTING);
        listener.onClosing();
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        setState(State.DISCONNECTED);
        logger.warning("Disconnected from server");
        pingHandler.removeLast();
        timeoutHandler.removeLast();
        processReconnection();
        listener.onClosed();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
        logger.warning("Connect error: " + throwable);
        setState(State.DISCONNECTED);
        pingHandler.removeLast();
        timeoutHandler.removeLast();
        processReconnection();
        listener.onFailure(throwable);
    }

    private void onTextMessage(String text) {
        try {
            logger.info("Receiving: " + text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject message = null;
        try {
            message = new JSONObject(text);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // ignore non-json messages
        }

        // Valid message - reschedule next ping
        reschedulePing();

        // Proccess PING messages or send the message downstream
        RPC.MsgType messageType = RPC.getMessageType(message.optString("msg"));
        if (messageType == RPC.MsgType.PING) {
            sendData(RPC.PONG_MESSAGE);
        } else {
            listener.onMessageReceived(message);
        }
    }

    /**
     * Function for connecting to server
     */

    protected void createSocket() {
        // Create a WebSocket with a socket connection timeout value.
        request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "gzip, deflate, sdch")
                .addHeader("Accept-Language", "en-US,en;q=0.8")
                .addHeader("Pragma", "no-cache")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36")
                .build();

        setState(State.CREATED);
    }

    public void connect() {
        setState(State.CONNECTING);
        ws = client.newWebSocket(request, this);
    }

    protected void connectAsync() {
        connect();
    }

    protected void sendDataInBackground(String message) {
        sendData(message);
    }

    public void sendData(String message) {
        if (getState() == State.CONNECTED) {
            logger.info("Sending: " + message);
            ws.send(message);
        }
    }

    public void reconnect() {
        logger.info("reconnecting");
        connect();
    }

    public void disconnect() {
        logger.info("Calling disconnect");
        if (currentState == State.DISCONNECTED) {
            return;
        } else if (currentState == State.CONNECTED) {
            ws.close(1001, "Close");
            setState(State.DISCONNECTING);
        } else {
            setState(State.DISCONNECTED);
        }

        pingHandler.removeLast();
        timeoutHandler.removeLast();
        selfDisconnect = true;
    }

    /* visible for testing */
    void processReconnection() {
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
                pingHandler.cancel();
                logger.info("Number of attempts are complete");
            }
        } else {
            pingHandler.cancel();
            selfDisconnect = false;
        }
    }

    // TODO: 15/8/17 solve problem of PONG RECEIVE FAILED by giving a fair chance
    protected void reschedulePing() {
        if (!pingEnable)
            return;

        logger.info("Scheduling ping in: " + pingInterval + " ms");
        pingHandler.removeLast();
        timeoutHandler.removeLast();
        pingHandler.postDelayed(new TimerTask() {
            @Override
            public void run() {
                logger.info("SENDING PING");
                sendData(RPC.PING_MESSAGE);
            }
        }, pingInterval);
        timeoutHandler.postDelayed(new TimerTask() {
            @Override
            public void run() {
                if (getState() != State.DISCONNECTING && getState() != State.DISCONNECTED) {
                    logger.warning("PONG RECEIVE FAILED");
                    ws.cancel();
                    //onFailure(ws, new IOException("PING Timeout"), null);
                }
                timeoutHandler.removeLast();
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

