package io.rocketchat.common.network;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by sachin on 7/6/17.
 */

public class Socket {

    String url;
    WebSocketFactory factory;
    protected WebSocket ws;

    protected Socket(String url){
        this.url=url;
        factory=new WebSocketFactory().setConnectionTimeout(5000);
    }

    /**
     * Function for connecting to server
     */

    protected void createSocket(){
        // Create a WebSocket with a socket connection timeout value.
        try {
            ws = factory.createSocket(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ws.addExtension("permessage-deflate; client_max_window_bits");
        ws.addHeader("Accept-Encoding","gzip, deflate, sdch");
        ws.addHeader("Accept-Language","en-US,en;q=0.8");
        ws.addHeader("Pragma","no-cache");
        ws.addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

    }

    protected void connect() {
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

    protected void connectAsync(){
        ws.connectAsynchronously();
    }

    protected void reconnect(){
        try {
            ws = ws.recreate(5000).connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
