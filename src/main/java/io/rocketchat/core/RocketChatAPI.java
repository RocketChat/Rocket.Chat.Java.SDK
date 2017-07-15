package io.rocketchat.core;
import io.rocketchat.common.network.Socket;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */
public class RocketChatAPI extends Socket {

    AtomicInteger integer;
    String sessionId;

    public RocketChatAPI(String url) {
        super(url);
        integer=new AtomicInteger(1);
    }

    public void connect(){
        createSocket();
        super.connect();
    }
}
