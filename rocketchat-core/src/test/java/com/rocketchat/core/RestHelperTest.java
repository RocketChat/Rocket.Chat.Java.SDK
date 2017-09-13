package com.rocketchat.core;

import com.rocketchat.core.provider.TokenProvider;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.fabric8.mockwebserver.DefaultMockServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(MockitoJUnitRunner.class)
public class RestHelperTest {

    RestHelper helper;
    OkHttpClient client;
    HttpUrl baseUrl;

    @Mock
    TokenProvider tokenProvider;

    DefaultMockServer mockServer;

    @Before
    public void setup() {
        mockServer = new DefaultMockServer();
        baseUrl = HttpUrl.parse(mockServer.url("/"));
        client = new OkHttpClient();

        helper = new RestHelper(client, baseUrl, tokenProvider);
    }
}
