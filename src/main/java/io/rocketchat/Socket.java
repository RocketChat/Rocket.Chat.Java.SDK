package io.rocketchat;

import com.neovisionaries.ws.client.*;
import io.rocketchat.rpc.BasicRPC;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 7/6/17.
 */

public class Socket {
    String url;
    WebSocketListener listener;
    private WebSocketFactory factory;
    private WebSocket ws;
    AtomicInteger integer;
    ConcurrentHashMap<Long,Object> callbacks;


    String sessionId;

    Socket(String url){
        this.url=url;
        listener=getListener();
        integer=new AtomicInteger(2);
        callbacks=new ConcurrentHashMap<Long, Object>();
    }

    WebSocketListener getListener() {
        return new WebSocketListener() {
            public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
//                System.out.println("on state changed");
            }

            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                integer.set(2);
                System.out.println("Connected to server");
            }

            public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("got connect error");
            }

            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                System.out.println("Disconnected to server");
            }

            public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("Got frame");
            }

            public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on continuation frame");
            }

            public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                //   System.out.println("On text frame");
            }

            public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on binary frame");
            }

            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On close frame");
            }

            public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("On ping frame" + frame.getPayloadText());
            }

            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on pong frame");
            }

            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                JSONObject object = new JSONObject(text);

                if (object.has("server_id")) {
                    websocket.sendText(BasicRPC.ConnectObject());
                } else {

                    if (object.optString("msg").equals("ping")) {
                        websocket.sendText("{\"msg\":\"pong\"}");
                    } else if (object.optString("msg").equals("connected")) {
                        sessionId = object.optString("session");
                        System.out.println("session id is "+sessionId);
                    }
//
                    System.out.println("Message is " + text);
                }
            }

            public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                System.out.println("on binary message");
            }

            public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("on sending frame");
            }

            public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
//                System.out.println("on frame set "+frame.getPayloadText());
            }

            public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                System.out.println("on frame unsent");
            }

            public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("On error");
            }

            public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("On frame error");
            }

            public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                System.out.println("On message error");
            }

            public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {
                System.out.println("on message decompression error");
            }

            public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                System.out.println("on text message error");
            }

            public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                System.out.println("on send error");
            }

            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
                System.out.println("on unexpected error");
            }

            public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
                System.out.println("handle callback error");
            }

            public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {
//                System.out.println("On sending handshake");
            }
        };


    }

    /**
     * Function for connecting to server
     * @throws IOException
     */
    public void connect() throws IOException {

        factory = new WebSocketFactory();

        // Create a WebSocket with a socket connection timeout value.
        ws = factory.createSocket(url);

        ws.addListener(listener);

        try
        {
            // Connect to the server and perform an opening handshake.
            // This method blocks until the opening handshake is finished.
            ws.connect();
        }
        catch (OpeningHandshakeException e)
        {
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
            for (Map.Entry<String, List<String>> entry : headers.entrySet())
            {
                // Header name.
                String name = entry.getKey();

                // Values of the header.
                List<String> values = entry.getValue();

                if (values == null || values.size() == 0)
                {
                    // Print the name only.
                    System.out.println(name);
                    continue;
                }

                for (String value : values)
                {
                    // Print the name and the value.
                    System.out.format("%s: %s\n", name, value);
                }
            }
        }
        catch (WebSocketException e)
        {
            System.out.println("Got websocket exception "+e.getMessage());
            // Failed to establish a WebSocket connection.
        }
    }

}
