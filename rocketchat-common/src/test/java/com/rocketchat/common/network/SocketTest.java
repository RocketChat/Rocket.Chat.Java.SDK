package com.rocketchat.common.network;

import com.rocketchat.common.SocketListener;
import com.rocketchat.common.data.model.MessageType;
import com.rocketchat.common.data.rpc.RPC;
import com.rocketchat.common.utils.Logger;
import com.rocketchat.common.utils.NoopLogger;

import io.fabric8.mockwebserver.DefaultMockServer;
import okhttp3.OkHttpClient;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SocketTest {

    @Mock
    private SocketListener listener;

    @Mock
    private Logger logger;

    @Captor
    private ArgumentCaptor<Throwable> throwableCaptor;

    @Captor
    private ArgumentCaptor<MessageType> messageTypeCaptor;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private DefaultMockServer mockServer;

    @Before
    public void setUp() {
        System.out.println("setup");
        mockServer = new DefaultMockServer();
        mockServer.start();
    }

    /**
     * Given a valid WebSocket URL
     * Then the Socket should connect
     */
    @Test
    public void testShouldConnectSuccesfuly() throws InterruptedException {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket().open().done().once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);

        socket.connect();
        verify(listener, Mockito.timeout(2000)).onConnected();

        socket.disconnect();
    }

    /**
     * Given a valid HTTP url that is not a websocket
     * Then the Socket should fail to connect.
     */
    @Test
    public void testShouldFailfIfNotWebSocket() {
        mockServer.expect().withPath("/notwebsocket").andReturn(200, "HTTP OK").once();
        Socket socket = new Socket(mockServer.url("/notwebsocket"), listener);
        socket.connect();

        verify(listener, Mockito.timeout(2000)).onFailure(throwableCaptor.capture());
        assertTrue(throwableCaptor.getValue() != null);

        socket.disconnect();
    }

    /**
     * Given a valid that doesn't respond 101 Switching Protocols
     * Then the Socket should fail
     */
    @Test
    public void testShouldFailfIfNotWebSocket_2() {
        Socket socket = new Socket(mockServer.url("/notwebsocket"), listener);
        socket.connect();

        verify(listener, Mockito.timeout(2000)).onFailure(throwableCaptor.capture());
        assertTrue(throwableCaptor.getValue() != null);

        socket.disconnect();
    }

    /**
     * Given a succesfully connected Socket
     * Then the Socket should be able to receive JSON messages
     */
    @Test
    public void testShouldIgnoreValidJsonWithoutMessageType() throws JSONException {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket()
                .open()
                .waitFor(500).andEmit("{\"key\":\"value\"}")
                .done()
                .once();

        Socket socket = new Socket(new OkHttpClient(), mockServer.url("/websocket"),
                logger, listener);
        socket.connect();

        verify(listener, timeout(200).times(0))
                .onMessageReceived(any(MessageType.class), any(String.class), any(String.class));

        verify(logger, timeout(200).times(1))
                .warning(messageCaptor.capture());
        assertTrue(messageCaptor.getValue() != null);
        assertThat(messageCaptor.getValue(), startsWith("Error parsing message: "));

        socket.disconnect();
    }

    /**
     * Given a succesfully connected Socket
     * Then the Socket should ignore invalid messages
     */
    @Test
    public void testShouldIgnoreInvalidJsonMessages() {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket()
                .open()
                .waitFor(100).andEmit("INVALID MESSAGE")
                .done()
                .once();

        Socket socket = new Socket(new OkHttpClient(), mockServer.url("/websocket"),
                logger, listener);
        socket.connect();

        verify(listener, timeout(2000).times(0))
                .onMessageReceived(any(MessageType.class), any(String.class), any(String.class));

        verify(logger, timeout(2000).times(1))
                .warning(messageCaptor.capture());
        assertTrue(messageCaptor.getValue() != null);
        assertThat(messageCaptor.getValue(), startsWith("Error parsing message: "));
        socket.disconnect();
    }

    /**
     * Given the Socket is connected and it receives a valid message
     * then the Socket should schedule a RPC.PING_MESSAGE in ping interval
     */
    @Test
    public void testShouldSchedulePingOnMessageReceived() {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket()
                .open()
                .waitFor(500).andEmit("{\"msg\":\"connected\"}")
                .expect(RPC.PING_MESSAGE).andEmit(RPC.PONG_MESSAGE).always()
                .done()
                .once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);
        socket.setPingInterval(1000);
        socket.connect();

        verify(listener, timeout(2000).times(2))
                .onMessageReceived(messageTypeCaptor.capture(), nullable(String.class),
                        messageCaptor.capture());
        assertThat(messageCaptor.getValue(), is(notNullValue()));
        assertThat(messageCaptor.getAllValues().size(), is(equalTo(2)));

        assertThat(messageTypeCaptor.getValue(), is(notNullValue()));;
        assertThat(messageTypeCaptor.getAllValues().size(), is(equalTo(2)));

        assertThat(messageTypeCaptor.getAllValues().get(0), is(equalTo(MessageType.CONNECTED)));
        assertThat(messageTypeCaptor.getAllValues().get(1), is(equalTo(MessageType.PONG)));

        assertThat(messageCaptor.getAllValues().get(0), is(equalTo("{\"msg\":\"connected\"}")));
        assertThat(messageCaptor.getAllValues().get(1), is(equalTo(RPC.PONG_MESSAGE)));

        socket.disconnect();
    }

    /**
     * Given the Socket is connected and it receives a Ping message
     * Then the Socket should respond with a Pong message
     */
    @Test
    public void testShouldPongAfterPing() {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket()
                .open()
                .waitFor(200).andEmit(RPC.PING_MESSAGE)
                .expect(RPC.PING_MESSAGE).andEmit(RPC.PING_MESSAGE).once()
                .done()
                .once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);
        socket.connect();
        socket.setPingInterval(5000);

        /*verify(listener, Mockito.timeout(2000)).onMessageReceived(messageTypeCaptor.capture(),
                nullable(String.class), messageCaptor.capture());
        assertThat(messageTypeCaptor.getValue(), is(notNullValue()));
        assertThat(messageTypeCaptor.getValue(), is(equalTo(MessageType.PING)));
        assertThat(messageCaptor.getValue(), is(notNullValue()));
        assertThat(messageCaptor.getValue(), is(equalTo(RPC.PING_MESSAGE)));*/
        verify(listener, timeout(2000).times(0))
                .onMessageReceived(any(MessageType.class), any(String.class), any(String.class));

        socket.disconnect();
    }

    /**
     * Given a succesfully connected Socket
     * Then it should disconnect after a Ping timeout
     */
    @Test
    public void testShouldDisconnectAfterPingTimeout() {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket()
                .open()
                .waitFor(100).andEmit(RPC.PONG_MESSAGE)
                .expect(RPC.PING_MESSAGE).andEmit("A").once()
                .done()
                .once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);
        socket.setPingInterval(500);
        socket.connect();

        verify(listener, Mockito.timeout(4000)).onFailure(throwableCaptor.capture());

        socket.disconnect();
    }

    /**
     * Given a succesfully connected Socket
     * And the Socket has a ReconnectionStrategy
     * Then it should attempt to reconnect after a Ping timeout
     */
    @Test
    public void testShouldReconnectAfterPingTimeout() {
        ReconnectionStrategy strategy = Mockito.mock(ReconnectionStrategy.class);
        given(strategy.getNumberOfAttempts()).willReturn(0, 1, 2, 3);
        given(strategy.getMaxAttempts()).willReturn(2);
        given(strategy.getReconnectInterval()).willReturn(1000);

        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket()
                .open()
                .waitFor(100).andEmit(RPC.PONG_MESSAGE)
                .expect(RPC.PING_MESSAGE).andEmit("A").once()
                .done()
                .once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);
        socket.setPingInterval(500);
        socket.setReconnectionStrategy(strategy);
        socket.connect();

        verify(listener, Mockito.timeout(4000)).onFailure(throwableCaptor.capture());
        verify(strategy, timeout(4000).times(3)).getMaxAttempts();
        verify(strategy, timeout(4000).times(3)).getNumberOfAttempts();
        verify(strategy, timeout(4000).times(2)).processAttempts();
        verify(strategy, timeout(4000).times(2)).getReconnectInterval();

        socket.disconnect();
    }

    /**
     * Given a succesfully connected Socket
     * And the Socket has a ReconnectionStrategy
     * Then it should attempt to reconnect after 'reconnectInterval'
     */
    @Test
    public void testShouldReconnectAfterReconnectingInterval() {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket().open().done().once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);
        socket.setReconnectionStrategy(new ReconnectionStrategy(1, 1000));

        // schedule a reconnection
        socket.processReconnection();

        verify(listener, Mockito.after(1200)).onConnected();
        socket.disconnect();
    }

    @Test
    public void testShouldDisconnectSocket() {
        mockServer.expect().withPath("/websocket")
                .andUpgradeToWebSocket().open()
                .expect("A").andEmit("B").once()
                .done()
                .once();

        Socket socket = new Socket(mockServer.url("/websocket"), listener);

        socket.connect();
        verify(listener, Mockito.timeout(500)).onConnected();

        socket.disconnect();
        verify(listener, Mockito.timeout(1000)).onClosing();
        verify(listener, Mockito.timeout(1500)).onClosed();
    }

    @After
    public void shutdown() {
        System.out.println("shutdown");
        mockServer.shutdown();
    }
}
