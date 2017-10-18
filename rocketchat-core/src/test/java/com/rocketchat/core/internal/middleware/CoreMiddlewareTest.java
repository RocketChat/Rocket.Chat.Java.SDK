package com.rocketchat.core.internal.middleware;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.RocketChatInvalidResponseException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.core.TestMessages;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.JsonAdapterFactory;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.Token;
import com.squareup.moshi.Moshi;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CoreMiddlewareTest {
    @Mock
    LoginCallback loginCallback;

    @Mock
    SimpleCallback simpleCallback;

    @Mock
    SimpleListCallback<RoomRole> roomRolesCallback;

    @Captor
    ArgumentCaptor<RocketChatException> errorArgumentCaptor;

    @Captor
    ArgumentCaptor<Token> tokenCaptor;

    CoreMiddleware middleware;

    JSONObject INVALID_RESPONSE;

    @Before
    public void setup() throws JSONException {
        Moshi moshi = new Moshi.Builder()
                .add(JsonAdapterFactory.create())
                .build();
        middleware = new CoreMiddleware(moshi);

        INVALID_RESPONSE = new JSONObject("{\"valid\":\"json\"}");
    }

    @Test
    public void testShouldEmitOnSuccessForSimpleCallback() throws JSONException {
        middleware.createCallback(1, simpleCallback, CoreMiddleware.CallbackType.MESSAGE_OP);
        middleware.processCallback(1, new JSONObject("{\"result\":\"ok\"}"));

        verify(simpleCallback).onSuccess();
    }

    @Test
    public void testShouldEmitSuccessOnLogin() throws JSONException {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        middleware.processCallback(1, new JSONObject(TestMessages.LOGIN_RESPONSE_OK));

        verify(loginCallback).onLoginSuccess(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getAuthToken(), is(equalTo("Yk_MNMp7K6A8J_3ytsC3rxwIZe9PZ4pfkPe-6G7JPYg")));
        assertThat(tokenCaptor.getValue().getUserId(), is(equalTo("yG6FQYRsuTWRK8KP6")));
    }

    @Test
    public void testShouldEmitOnErrorOnLogin() throws JSONException {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        middleware.processCallback(1, new JSONObject(TestMessages.LOGIN_RESPONSE_FAIL));

        verify(loginCallback).onError(errorArgumentCaptor.capture());
        RocketChatApiException error = (RocketChatApiException) errorArgumentCaptor.getValue();
        assertTrue(error.getError() == 403);
        assertThat(error.getReason(), is(equalTo("User not found")));
    }

    @Test
    public void testShouldEmitErrorForAllCallbacksOnNotifyDisconnection() {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        middleware.createCallback(2, simpleCallback, CoreMiddleware.CallbackType.DELETE_GROUP);

        middleware.notifyDisconnection("Testing disconnection");

        verify(loginCallback).onError(errorArgumentCaptor.capture());
        assertThat(errorArgumentCaptor.getValue(), instanceOf(RocketChatNetworkErrorException.class));
        RocketChatNetworkErrorException networkError = (RocketChatNetworkErrorException) errorArgumentCaptor.getValue();
        assertThat(networkError.getMessage(), is(equalTo("Testing disconnection")));

        verify(simpleCallback).onError(errorArgumentCaptor.capture());
        assertThat(errorArgumentCaptor.getValue(), instanceOf(RocketChatNetworkErrorException.class));
        networkError = (RocketChatNetworkErrorException) errorArgumentCaptor.getValue();
        assertThat(networkError.getMessage(), is(equalTo("Testing disconnection")));

        verifyNoMoreInteractions(loginCallback);
        verifyNoMoreInteractions(simpleCallback);
    }

    @Test
    public void shouldEmitErrorWithInvalidResponse() {
        middleware.createCallback(1, simpleCallback, CoreMiddleware.CallbackType.ARCHIVE);
        middleware.processCallback(1, INVALID_RESPONSE);

        verify(simpleCallback).onError(errorArgumentCaptor.capture());
        verifyNoMoreInteractions(simpleCallback);
        assertThat(errorArgumentCaptor.getValue(), instanceOf(RocketChatInvalidResponseException.class));
        RocketChatInvalidResponseException exception = (RocketChatInvalidResponseException) errorArgumentCaptor.getValue();
        assertThat(exception.getMessage(), is(equalTo("Missing \"result\" or \"error\" values: {\"valid\":\"json\"}")));
    }

    // TODO - add more tests...
}
