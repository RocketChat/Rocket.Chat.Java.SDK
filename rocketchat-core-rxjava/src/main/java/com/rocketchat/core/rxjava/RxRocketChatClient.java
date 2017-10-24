package com.rocketchat.core.rxjava;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.utils.RocketChatVerifier;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Token;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.OkHttpClient;

public class RxRocketChatClient implements ConnectListener {

    private final RocketChatClient client;

    private BehaviorSubject<State> stateMachine;

    public RxRocketChatClient(RocketChatClient client) {
        this.client = client;
        stateMachine = BehaviorSubject.createDefault(State.disconnected());
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

    public Flowable<State> connect() {
        client.connect(this);
        return stateMachine.toFlowable(BackpressureStrategy.BUFFER);
    }

    public static Completable verifyServerVersion(final OkHttpClient client, final String baseUrl,
                                                  final RocketChatVerifier.VersionVerifier verifier) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                RocketChatVerifier.checkServerVersion(client, baseUrl, verifier,
                        new SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                emitter.onComplete();
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
        stateMachine.onNext(State.connected());
    }

    @Override
    public void onDisconnect(boolean closedByServer) {
        stateMachine.onNext(State.disconnected());
    }

    @Override
    public void onConnectError(Throwable websocketException) {
        stateMachine.onNext(State.connectError());
    }

    public static class State {
        private Status currentStatus;

        private State(Status status) {
            this.currentStatus = status;
        }

        public static State disconnected() {
            return new State(Status.DISCONNECTED);
        }

        public static State connecting() {
            return new State(Status.CONNECTING);
        }

        public static State connected() {
            return new State(Status.CONNECTED);
        }

        public static State authenticated() {
            return new State(Status.AUTHENTICATED);
        }

        public static State disconnecting() {
            return new State(Status.DISCONNECTING);
        }

        public static State connectError() {
            return new State(Status.CONNECT_ERROR);
        }

        public static State waitingNetwork() {
            return new State(Status.WAITING_NETWORK);
        }

        public static State waitingToConnect() {
            return new State(Status.WAITING_TO_CONNECT);
        }
    }
}
