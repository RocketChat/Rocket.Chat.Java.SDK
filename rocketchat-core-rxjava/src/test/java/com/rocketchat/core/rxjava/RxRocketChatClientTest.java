package com.rocketchat.core.rxjava;

import com.rocketchat.common.RocketChatAuthException;
import com.rocketchat.common.RocketChatNetworkErrorException;
import com.rocketchat.core.RocketChatClient;
import com.rocketchat.core.callback.LoginCallback;
import com.rocketchat.core.model.Token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class RxRocketChatClientTest {
    RxRocketChatClient sut;

    @Mock
    RocketChatClient api;

    Token token = new Token("userId", "token", null);

    @Before
    public void setup() {
        sut = new RxRocketChatClient(api);
    }

    @Test
    public void tesSigninShouldBeSuccessfull() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((LoginCallback) invocation.getArguments()[2]).onLoginSuccess(token);
                return null;
            }
        }).when(api).signin(any(String.class), any(String.class), any(LoginCallback.class));

        TestObserver<Token> testObserver = sut.signin("username", "password").test();

        testObserver.assertValue(new Predicate<Token>() {
            @Override
            public boolean test(Token token) throws Exception {
                return token != null && token.getUserId().contentEquals("userId")
                        && token.getAuthToken().contentEquals("token");
            }
        });
        testObserver.assertComplete();
        testObserver.assertNoErrors();
    }

    @Test
    public void testSigninShouldFailWithNullUsername() {
        doThrow(new IllegalArgumentException("username == null"))
                .when(api).signin((String) isNull(), any(String.class), any(LoginCallback.class));

        TestObserver<Token> testObserver = sut.signin(null, "password").test();

        testObserver.assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSigninShouldFailWithNullPassword() {
        doThrow(new IllegalArgumentException("password == null"))
                .when(api).signin(any(String.class), (String) isNull(), any(LoginCallback.class));

        TestObserver<Token> testObserver = sut.signin("username", null).test();

        testObserver.assertError(IllegalArgumentException.class);
    }

    @Test
    public void testSigninShouldFailWithRocketChatNetworkErrorException() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((LoginCallback) invocation.getArguments()[2]).onError(new RocketChatNetworkErrorException("network error"));
                return null;
            }
        }).when(api).signin(any(String.class), any(String.class), any(LoginCallback.class));

        TestObserver<Token> testObserver = sut.signin("username", "password").test();
        testObserver.assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                return throwable != null
                        && throwable instanceof RocketChatNetworkErrorException
                        && throwable.getMessage().contentEquals("network error");
            }
        });
        testObserver.assertNoValues();
    }

    @Test
    public void testSigninShouldFailWithRocketChatAuthException() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((LoginCallback) invocation.getArguments()[2]).onError(new RocketChatAuthException("Invalid credentials"));
                return null;
            }
        }).when(api).signin(any(String.class), any(String.class), any(LoginCallback.class));

        TestObserver<Token> testObserver = sut.signin("username", "password").test();
        testObserver.assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception {
                return throwable != null
                        && throwable instanceof RocketChatAuthException
                        && throwable.getMessage().contentEquals("Invalid credentials");
            }
        });
        testObserver.assertNoValues();
    }
}
