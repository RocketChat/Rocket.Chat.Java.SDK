package com.rocketchat.core;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.core.callback.LoginListener;
import com.rocketchat.core.model.TokenObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.fabric8.mockwebserver.DefaultMockServer;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class RocketChatApiTest {

    @Mock
    LoginListener loginListener;
    @Captor
    ArgumentCaptor<TokenObject> tokenArgumentCaptor;
    @Captor
    ArgumentCaptor<ApiError> errorArgumentCaptor;
    private DefaultMockServer server;
    private RocketChatAPI api;

    @Before
    public void setUp() {
        server = new DefaultMockServer();
        server.start();

        api = new RocketChatAPI.Builder()
                .restBaseUrl(server.url("/api/v1/"))
                .websocketUrl(server.url("/websocket"))
                .build();
        api.disablePing();
    }

    @Test
    public void testShouldLoginSuccessfully() throws InterruptedException {
        TestUtils.setupMockServer(api, server,
                TestUtils.pair(TestMessages.LOGIN_REQUEST,
                        TestMessages.LOGIN_RESPONSE_OK));

        api.login("testuserrocks", "testuserrocks", loginListener);

        verify(loginListener, timeout(500)).onLoginSuccess(tokenArgumentCaptor.capture());
        verify(loginListener, never()).onLoginError(any(ApiError.class));
        TokenObject token = tokenArgumentCaptor.getValue();
        assertTrue(token != null);
        assertTrue(token.getAuthToken().contentEquals("Yk_MNMp7K6A8J_3ytsC3rxwIZe9PZ4pfkPe-6G7JPYg"));
        assertTrue(token.getUserId().contentEquals("yG6FQYRsuTWRK8KP6"));
        assertTrue(token.getExpiry().getTime() == 1511909570220L);

        api.disconnect();
    }

    @Test
    public void testShouldFailLoginWithWrongPassword() throws InterruptedException {
        TestUtils.setupMockServer(api, server,
                TestUtils.pair(TestMessages.LOGIN_REQUEST_FAIL,
                        TestMessages.LOGIN_RESPONSE_FAIL));

        api.login("testuserrocks", "wrongpassword", loginListener);

        verify(loginListener, timeout(500)).onLoginError(errorArgumentCaptor.capture());
        verify(loginListener, never()).onLoginSuccess(any(TokenObject.class));
        ApiError error = errorArgumentCaptor.getValue();
        assertTrue(error != null);
        assertTrue(error.getError() == 403);
        assertTrue(error.getReason().contentEquals("User not found"));
        assertTrue(error.getMessage().contentEquals("User not found [403]"));
        assertTrue(error.getErrorType().contentEquals("Meteor.Error"));

        api.disconnect();
    }

    @Test
    public void testShouldResumeLogin() throws InterruptedException {
        TestUtils.setupMockServer(api, server,
                TestUtils.pair(TestMessages.LOGIN_RESUME_REQUEST,
                        TestMessages.LOGIN_RESUME_RESPONSE_OK));

        api.loginUsingToken("tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdRzQKD", loginListener);
        verify(loginListener, never()).onLoginError(any(ApiError.class));
        verify(loginListener).onLoginSuccess(tokenArgumentCaptor.capture());
        TokenObject token = tokenArgumentCaptor.getValue();
        assertTrue(token != null);
        assertTrue(token.getAuthToken().contentEquals("tHKn4H62mdBi_gh5hjjqmu-x4zdZRAYiiluqpdRzQKD"));
        assertTrue(token.getUserId().contentEquals("yG6FQYRsuTWRK8KP6"));
        assertTrue(token.getExpiry().getTime() == 0L);

        api.disconnect();
    }

    @Test
    public void testShouldFailResumeLoginWithWrongToken() throws InterruptedException {
        TestUtils.setupMockServer(api, server,
                TestUtils.pair(TestMessages.LOGIN_RESUME_REQUEST_FAIL,
                        TestMessages.LOGIN_RESUME_RESPONSE_FAIL));

        api.loginUsingToken("INVALID_TOKEN", loginListener);
        verify(loginListener, never()).onLoginSuccess(any(TokenObject.class));
        verify(loginListener).onLoginError(errorArgumentCaptor.capture());

        ApiError error = errorArgumentCaptor.getValue();
        assertTrue(error != null);
        assertTrue(error.getError() == 403);
        assertTrue(error.getReason().contentEquals("You've been logged out by the server. Please log in again."));
        assertTrue(error.getMessage().contentEquals("You've been logged out by the server. Please log in again. [403]"));
        assertTrue(error.getErrorType().contentEquals("Meteor.Error"));

        api.disconnect();
    }

    @After
    public void shutdown() {
        verifyNoMoreInteractions(loginListener);
        System.out.println("shutdown");
        server.shutdown();
    }
}
