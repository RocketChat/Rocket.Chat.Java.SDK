package io.rocketchat.core;
import io.rocketchat.common.data.rpc.RPC;
import io.rocketchat.common.network.Socket;
import io.rocketchat.core.callback.LoginListener;
import io.rocketchat.core.middleware.CoreMiddleware;
import io.rocketchat.core.rpc.BasicRPC;
import io.rocketchat.common.listener.ConnectListener;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sachin on 8/6/17.
 */
public class RocketChatAPI extends Socket {

    AtomicInteger integer;
    String sessionId;
    JSONObject userInfo;

    ConnectListener connectListener;

    CoreMiddleware coreMiddleware;

    public RocketChatAPI(String url) {
        super(url);
        integer=new AtomicInteger(1);
        coreMiddleware=CoreMiddleware.getInstance();
    }

    public void login(String username, String password, LoginListener loginListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,loginListener, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.login(uniqueID,username,password));
    }


    public void loginUsingToken(String token,LoginListener loginListener){
        int uniqueID=integer.getAndIncrement();
        coreMiddleware.createCallback(uniqueID,loginListener, CoreMiddleware.ListenerType.LOGIN);
        sendDataInBackground(BasicRPC.loginUsingToken(uniqueID,token));
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void connect(ConnectListener connectListener){
        createSocket();
        this.connectListener = connectListener;
        super.connectAsync();
    }

    @Override
    protected void onConnected() {
        integer.set(1);
        sendDataInBackground(BasicRPC.ConnectObject());
        super.onConnected();
    }

    @Override
    protected void onTextMessage(String text) throws Exception {
        JSONObject object = new JSONObject(text);
        switch (RPC.parse(object.optString("msg"))) {
            case PING:
                sendDataInBackground("{\"msg\":\"pong\"}");
                break;
            case CONNECTED:
                sessionId = object.optString("session");
                if (connectListener != null) {
                    connectListener.onConnect(sessionId);
                }
                break;
            case ADDED:
                if (object.optString("collection").equals("users")) {
                    userInfo = object.optJSONObject("fields");
                }
                break;
            case RESULT:
                coreMiddleware.processCallback(Long.valueOf(object.optString("id")), object);
                break;
            case READY:
                break;
            case CHANGED:
                break;
            case OTHER:
                break;
        }

        super.onTextMessage(text);
    }

    @Override
    protected void onConnectError(Exception websocketException) {
        if (connectListener!=null) {
            connectListener.onConnectError(websocketException);
        }
        super.onConnectError(websocketException);
    }

    @Override
    protected void onDisconnected(boolean closedByServer) {
        if (connectListener!=null) {
            connectListener.onDisconnect(closedByServer);
        }
        super.onDisconnected(closedByServer);
    }
}
