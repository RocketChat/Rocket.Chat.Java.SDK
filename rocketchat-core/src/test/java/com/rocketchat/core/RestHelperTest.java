package com.rocketchat.core;

import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.TokenObject;
import com.rocketchat.core.provider.TokenProvider;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.fabric8.mockwebserver.DefaultMockServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RestHelperTest {

    RestHelper helper;
    OkHttpClient client;
    HttpUrl baseUrl;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    LoginCallback loginCallback;

    @Captor
    ArgumentCaptor<TokenObject> tokenCaptor;

    @Captor
    ArgumentCaptor<RocketChatException> exceptionCaptor;

    DefaultMockServer mockServer;

    @Before
    public void setup() {
        mockServer = new DefaultMockServer();
        mockServer.start();

        baseUrl = HttpUrl.parse(mockServer.url("/"));
        client = new OkHttpClient();

        helper = new RestHelper(client, baseUrl, tokenProvider);
    }

    // start signin tests
    @Test
    public void tesSigninShouldBeSuccessfull() {
        mockServer.expect().post().withPath("/login")
                .andReturn(200, "{\"status\": \"success\",\"data\": {\"authToken\": \"token\",\"userId\": \"userid\"}}").once();

        helper.signin("user", "password", loginCallback);

        verify(loginCallback, timeout(100).only()).onLoginSuccess(tokenCaptor.capture());
        TokenObject token = tokenCaptor.getValue();
        assertThat(token.getUserId(), is(equalTo("userid")));
        assertThat(token.getAuthToken(), is(equalTo("token")));
        assertThat(token.getExpiry(), is(nullValue()));
    }

    @Test
    public void testSigninShouldFailOnInvalidJson() {
        mockServer.expect().post().withPath("/login")
                .andReturn(200, "NOT A JSON").once();

        helper.signin("user", "password", loginCallback);
        verify(loginCallback, timeout(100).only()).onError(exceptionCaptor.capture());
        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception, is(instanceOf(RocketChatInvalidResponseException.class)));
        assertThat(exception.getMessage(), is(equalTo("A JSONObject text must begin with '{' at character 1")));
        assertThat(exception.getCause(), is(instanceOf(JSONException.class)));
    }

    @Test
    public void testSiginShouldFailWithAuthExceptionOn401() {
        mockServer.expect().post().withPath("/login")
                .andReturn(401, "{\"status\": \"error\",\"message\": \"Unauthorized\"}").once();

        helper.signin("user", "password", loginCallback);
        verify(loginCallback, timeout(200).only()).onError(exceptionCaptor.capture());
        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception, is(instanceOf(RocketChatAuthException.class)));
        assertThat(exception.getMessage(), is(equalTo("Invalid credentials")));
    }

    @Test
    public void testSigninShouldFailIfNot2xx() {
        helper.signin("user", "password", loginCallback);
        verify(loginCallback, timeout(200).only()).onError(exceptionCaptor.capture());
        RocketChatException exception = exceptionCaptor.getValue();
        assertThat(exception, is(instanceOf(RocketChatException.class)));

    }
    // end signin tests

    @After
    public void cleanup() {
        mockServer.shutdown();
    }
}
