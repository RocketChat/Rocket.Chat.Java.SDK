package com.rocketchat.core.rxjava;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Token;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class RxRocketChatClient implements ConnectListener {

    private final RocketChatClient client;

    public RxRocketChatClient(RocketChatClient client) {
        this.client = client;
    }

    public Single<Token> signin(final String username, final String password) {
        return Single.create(new SingleOnSubscribe<Token>() {
            @Override
            public void subscribe(final SingleEmitter<Token> emitter) throws Exception {
                client.signin(username, password, new LoginCallback() {
                    @Override
                    public void onLoginSuccess(Token token) {
                        emitter.onSuccess(token);
                    }

                    @Override
                    public void onError(RocketChatException error) {
                        emitter.onError(error);
                    }
                });
            }
        });
    }

    @Override
    public void onConnect(String sessionID) {

    }

    @Override
    public void onDisconnect(boolean closedByServer) {

    }

    @Override
    public void onConnectError(Throwable websocketException) {

    }
}
