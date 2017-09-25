package com.rocketchat.core;

import com.rocketchat.common.listener.ConnectListener;
import com.rocketchat.common.utils.Pair;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.fabric8.mockwebserver.DefaultMockServer;
import io.fabric8.mockwebserver.dsl.EventDoneable;
import io.fabric8.mockwebserver.dsl.TimesOnceableOrHttpHeaderable;

public class TestUtils {
    @SafeVarargs
    public static void setupMockServer(RocketChatClient api, DefaultMockServer server,
                                       Pair<Object, String>... expectEmit) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] connected = {false};

        EventDoneable<TimesOnceableOrHttpHeaderable<Void>> expectation = server
                .expect().withPath("/websocket").andUpgradeToWebSocket()
                .open()
                .expect(TestMessages.CONNECT_REQUEST)
                .andEmit(TestMessages.CONNECT_RESPONSE_OK).once();
        for (Pair<Object, String> pair : expectEmit) {
            if (pair.first instanceof Long || pair.first instanceof Integer) {
                expectation = expectation.waitFor((Long) pair.first).andEmit(pair.second);
            } else {
                expectation = expectation.expect(pair.first).andEmit(pair.second).once();
            }
        }
        expectation.done().once();

        api.connect(new ConnectListener() {
            @Override
            public void onConnect(String sessionID) {
                connected[0] = true;
                latch.countDown();
            }

            @Override
            public void onDisconnect(boolean closedByServer) {
            }

            @Override
            public void onConnectError(Throwable websocketException) {
                websocketException.printStackTrace();
                latch.countDown();
            }
        });

        latch.await(300, TimeUnit.MILLISECONDS);
        if (!connected[0]) {
            throw new IllegalStateException("not connected...");
        }
    }

    public static Pair<Object, String> pair(String expect, String emit) {
        return Pair.create((Object) expect, emit);
    }

    public static Pair<Object, String> pair(long wait, String emit) {
        return Pair.create((Object) wait, emit);
    }
}