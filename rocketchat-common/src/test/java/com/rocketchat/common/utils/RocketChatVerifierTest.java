package com.rocketchat.common.utils;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.listener.SimpleCallback;
import com.squareup.moshi.JsonEncodingException;
import io.fabric8.mockwebserver.DefaultMockServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RocketChatVerifierTest {

    @Mock
    private RocketChatVerifier.VersionVerifier verifier;

    @Mock
    private SimpleCallback callback;

    @Captor
    ArgumentCaptor<String> versionCaptor;

    @Captor
    private ArgumentCaptor<RocketChatException> exceptionCaptor;

    private DefaultMockServer mockServer;
    private OkHttpClient client;
    private HttpUrl baseUrl;

    @Before
    public void setup() {
        mockServer = new DefaultMockServer();
        mockServer.start();

        baseUrl = HttpUrl.parse(mockServer.url("/"));
        client = new OkHttpClient();
    }

    @Test
    public void shouldNotCallVersionVerifierOnNetworkError() {
        mockServer.shutdown();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(verifier, timeout(1000).times(0))
                .isValidVersion(versionCaptor.capture());
    }

    @Test
    public void shouldNotCallVersionVerifierWithInvalidResponse() {
        mockServer.expect().withPath("/api/info").andReturn(200, "INVALID_RESPONSE").once();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(verifier, timeout(1000).times(0))
                .isValidVersion(versionCaptor.capture());
    }

    @Test
    public void shouldCallVersionVerifierWithRightVersion() {
        mockServer.expect().withPath("/api/info")
                .andReturn(200, "{\"version\":\"0.59.0\"}").once();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(verifier, timeout(1000)).isValidVersion(versionCaptor.capture());
        assertThat(versionCaptor.getValue(), is(equalTo("0.59.0")));
    }

    @Test
    public void shouldCallCallbackOnNetworkError() {
        mockServer.shutdown();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(callback, timeout(1000)
                .times(1))
                .onError(exceptionCaptor.capture());

        assertThat(exceptionCaptor.getValue(),
                is(instanceOf(RocketChatNetworkErrorException.class)));
    }

    @Test
    public void shouldCallCallbackOnInvalidResponse() {
        mockServer.expect().withPath("/api/info").andReturn(200, "INVALID_RESPONSE").once();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(callback, timeout(1000)
                .times(1))
                .onError(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue(),
                is(instanceOf(RocketChatException.class)));
        assertThat(exceptionCaptor.getValue().getCause(),
                is(instanceOf(JsonEncodingException.class)));
        assertThat(exceptionCaptor.getValue().getMessage(),
                containsString("Use JsonReader.setLenient"));
    }

    @Test
    public void shouldSuccessOnVersionVerified() {
        given(verifier.isValidVersion(anyString())).willReturn(true);

        mockServer.expect().withPath("/api/info")
                .andReturn(200, "{\"version\":\"0.59.0\"}").once();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(callback, timeout(1000).times(1)).onSuccess();
    }

    @Test
    public void shouldFailIfVersionNotVerified() {
        given(verifier.isValidVersion(anyString())).willReturn(false);

        mockServer.expect().withPath("/api/info")
                .andReturn(200, "{\"version\":\"0.59.0\"}").once();
        RocketChatVerifier.checkServerVersion(client, baseUrl.toString(), verifier, callback);

        verify(callback, timeout(1000).times(1))
                .onError(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue(), is(instanceOf(RocketChatException.class)));
        assertThat(exceptionCaptor.getValue().getMessage(),
                is(equalTo("Unsupported version: 0.59.0")));
    }

    @After
    public void cleanup() {
        mockServer.shutdown();
    }
}
