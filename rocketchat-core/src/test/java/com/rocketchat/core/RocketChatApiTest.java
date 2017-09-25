package com.rocketchat.core;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.SocketListener;
import com.rocketchat.common.network.Socket;
import com.rocketchat.common.network.SocketFactory;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Token;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import io.fabric8.mockwebserver.DefaultMockServer;
import okhttp3.OkHttpClient;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RocketChatApiTest {

    @Mock
    LoginCallback loginCallback;

    @Mock
    Socket mockedSocket;

    @Captor
    ArgumentCaptor<Token> tokenArgumentCaptor;

    @Captor
    ArgumentCaptor<RocketChatApiException> errorArgumentCaptor;

    private DefaultMockServer server;
    private RocketChatClient sut;
    private SocketListener listener;

    @Before
    public void setUp() {
        /*server = new DefaultMockServer();
        server.start();*/

        String socketUrl = "https://test.rocket.chat/websocket";
        String restUrl = "https://test.rocket.chat/sut/v1/";

        sut = new RocketChatClient.Builder()
                /*.restBaseUrl(server.url("/sut/v1/"))
                .websocketUrl(server.url("/websocket"))*/
                .restBaseUrl(restUrl)
                .websocketUrl(socketUrl)
                .socketFactory(new SocketFactory() {
                    @Override
                    public Socket create(OkHttpClient client, String url, SocketListener socketListener) {
                        listener = socketListener;
                        return mockedSocket;
                    }
                })
                .build();
        sut.disablePing();

        when(loginCallback.getClassType()).thenReturn(LoginCallback.class);
    }

    @Test
    public void testShouldLoginSuccessfully() throws InterruptedException {
        /*TestUtils.setupMockServer(sut, server,
                TestUtils.pair(TestMessages.LOGIN_REQUEST,
                        TestMessages.LOGIN_RESPONSE_OK));*/

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listener.onMessageReceived(new JSONObject(TestMessages.LOGIN_RESPONSE_OK));
                return null;
            }
        }).when(mockedSocket).sendData(TestMessages.LOGIN_REQUEST);

        sut.login("testuserrocks", "testuserrocks", loginCallback);

        verify(loginCallback).getClassType();
        verify(loginCallback).onLoginSuccess(tokenArgumentCaptor.capture());
        verify(loginCallback, never()).onError(any(RocketChatApiException.class));
        Token token = tokenArgumentCaptor.getValue();
        assertTrue(token != null);
        assertTrue(token.getAuthToken().contentEquals("Yk_MNMp7K6A8J_3ytsC3rxwIZe9PZ4pfkPe-6G7JPYg"));
        assertTrue(token.getUserId().contentEquals("yG6FQYRsuTWRK8KP6"));
        assertTrue(token.getExpiry().getTime() == 1511909570220L);

        sut.disconnect();
    }

    @Test
    public void testShouldFailLoginWithWrongPassword() throws InterruptedException {
        /*TestUtils.setupMockServer(sut, server,
                TestUtils.pair(TestMessages.LOGIN_REQUEST_FAIL,
                        TestMessages.LOGIN_RESPONSE_FAIL));*/

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listener.onMessageReceived(new JSONObject(TestMessages.LOGIN_RESPONSE_FAIL));
                return null;
            }
        }).when(mockedSocket).sendData(TestMessages.LOGIN_REQUEST_FAIL);

        sut.login("testuserrocks", "wrongpassword", loginCallback);

        verify(loginCallback).getClassType();
        verify(loginCallback).onError(errorArgumentCaptor.capture());
        verify(loginCallback, never()).onLoginSuccess(any(Token.class));
        RocketChatApiException error = errorArgumentCaptor.getValue();
        assertTrue(error != null);
        assertTrue(error.getError() == 403);
        assertTrue(error.getReason().contentEquals("User not found"));
        assertTrue(error.getMessage().contentEquals("User not found [403]"));
        assertTrue(error.getErrorType().contentEquals("Meteor.Error"));

        sut.disconnect();
    }

    @Test
    public void testShouldResumeLogin() throws InterruptedException {
        /*TestUtils.setupMockServer(sut, server,
                TestUtils.pair(TestMessages.LOGIN_RESUME_REQUEST,
                        TestMessages.LOGIN_RESUME_RESPONSE_OK));*/

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listener.onMessageReceived(new JSONObject(TestMessages.LOGIN_RESUME_RESPONSE_OK));
                return null;
            }
        }).when(mockedSocket).sendData(TestMessages.LOGIN_RESUME_REQUEST);

        sut.loginUsingToken("tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdRzQKD", loginCallback);
        verify(loginCallback).getClassType();
        verify(loginCallback, times(1)).onLoginSuccess(tokenArgumentCaptor.capture());
        verify(loginCallback, never()).onError(any(RocketChatApiException.class));
        Token token = tokenArgumentCaptor.getValue();
        assertTrue(token != null);
        assertTrue(token.getAuthToken().contentEquals("tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdRzQKD"));
        assertTrue(token.getUserId().contentEquals("yG6FQYRsuTWRK8KP6"));
        assertTrue(token.getExpiry().getTime() == 0L);

        sut.disconnect();
    }

    @Test
    public void testShouldFailResumeLoginWithWrongToken() throws InterruptedException {
        /*TestUtils.setupMockServer(sut, server,
                TestUtils.pair(TestMessages.LOGIN_RESUME_REQUEST_FAIL,
                        TestMessages.LOGIN_RESUME_RESPONSE_FAIL));*/

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listener.onMessageReceived(new JSONObject(TestMessages.LOGIN_RESUME_RESPONSE_FAIL));
                return null;
            }
        }).when(mockedSocket).sendData(TestMessages.LOGIN_RESUME_REQUEST_FAIL);

        sut.loginUsingToken("INVALID_TOKEN", loginCallback);
        verify(loginCallback).getClassType();
        verify(loginCallback).onError(errorArgumentCaptor.capture());
        verify(loginCallback, never()).onLoginSuccess(any(Token.class));

        RocketChatApiException error = errorArgumentCaptor.getValue();
        assertTrue(error != null);
        assertTrue(error.getError() == 403);
        assertTrue(error.getReason().contentEquals("You've been logged out by the server. Please log in again."));
        assertTrue(error.getMessage().contentEquals("You've been logged out by the server. Please log in again. [403]"));
        assertTrue(error.getErrorType().contentEquals("Meteor.Error"));

        sut.disconnect();
    }

    @After
    public void shutdown() {
        verifyNoMoreInteractions(loginCallback);
        System.out.println("shutdown");
        //server.shutdown();
    }
}
