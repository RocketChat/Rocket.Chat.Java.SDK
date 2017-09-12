package com.rocketchat.core.middleware;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.data.model.Error;
import com.rocketchat.common.data.model.NetworkError;
import com.rocketchat.common.listener.SimpleCallback;
import com.rocketchat.common.listener.SimpleListCallback;
import com.rocketchat.core.TestMessages;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.RoomRole;
import com.rocketchat.core.model.TokenObject;

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
import static org.mockito.BDDMockito.given;
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
    ArgumentCaptor<Error> errorArgumentCaptor;

    @Captor
    ArgumentCaptor<TokenObject> tokenCaptor;

    CoreMiddleware middleware;

    @Before
    public void setup() {
        middleware = new CoreMiddleware();
        // Call real getClassType on mocked interfaces...
        given(loginCallback.getClassType()).willCallRealMethod();
        given(simpleCallback.getClassType()).willCallRealMethod();
        given(roomRolesCallback.getClassType()).willCallRealMethod();
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
    public void testShouldEmirOnErrorOnLogin() throws JSONException {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        middleware.processCallback(1, new JSONObject(TestMessages.LOGIN_RESPONSE_FAIL));

        verify(loginCallback).onError(errorArgumentCaptor.capture());
        ApiError error = (ApiError) errorArgumentCaptor.getValue();
        assertTrue(error.getError() == 403);
        assertThat(error.getReason(), is(equalTo("User not found")));
    }

    @Test(expected = ClassCastException.class)
    public void testShouldCrashWithWrongCallbackType() throws JSONException {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.MESSAGE_OP);
    }

    @Test(expected = ClassCastException.class)
    public void testShouldCrashWithWrongCallbackType2() throws JSONException {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.GET_ROOM_ROLES);
    }

    @Test(expected = ClassCastException.class)
    public void testShouldCrashWithWrongCallbackType3() throws JSONException {
        middleware.createCallback(1, roomRolesCallback, CoreMiddleware.CallbackType.GET_PERMISSIONS);
    }

    @Test
    public void testShouldEmitErrorForAllCallbacksOnNotifyDisconnection() {
        middleware.createCallback(1, loginCallback, CoreMiddleware.CallbackType.LOGIN);
        middleware.createCallback(2, simpleCallback, CoreMiddleware.CallbackType.DELETE_GROUP);

        middleware.notifyDisconnection("Testing disconnection");

        verify(loginCallback).getClassType();
        verify(loginCallback).onError(errorArgumentCaptor.capture());
        assertThat(errorArgumentCaptor.getValue(), instanceOf(NetworkError.class));
        NetworkError networkError = (NetworkError) errorArgumentCaptor.getValue();
        assertThat(networkError.getMessage(), is(equalTo("Testing disconnection")));

        verify(simpleCallback).getClassType();
        verify(simpleCallback).onError(errorArgumentCaptor.capture());
        assertThat(errorArgumentCaptor.getValue(), instanceOf(NetworkError.class));
        networkError = (NetworkError) errorArgumentCaptor.getValue();
        assertThat(networkError.getMessage(), is(equalTo("Testing disconnection")));

        verifyNoMoreInteractions(loginCallback);
        verifyNoMoreInteractions(simpleCallback);
    }

    // TODO - add more tests...
}
