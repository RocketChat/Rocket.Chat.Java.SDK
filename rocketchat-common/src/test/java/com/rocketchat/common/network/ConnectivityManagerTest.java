package com.rocketchat.common.network;

import com.rocketchat.common.listener.ConnectListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectivityManagerTest {

    @Mock
    ConnectListener listener;

    @Captor
    ArgumentCaptor<String> stringCaptor;
    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;
    @Captor
    ArgumentCaptor<Throwable> throwableCaptor;

    private ConnectivityManager manager;

    @Before
    public void setup() {
        manager = new ConnectivityManager();
        manager.register(listener);
    }

    @Test
    public void testConnectEvent() {
        manager.publishConnect("sessionId");

        verify(listener, only()).onConnect(stringCaptor.capture());
        String id = stringCaptor.getValue();
        assertThat(id, is(equalTo("sessionId")));
    }

    @Test
    public void testDisconnectEvent() {
        manager.publishDisconnect(true);
        manager.publishDisconnect(false);

        verify(listener, times(2)).onDisconnect(booleanCaptor.capture());
        assertThat(booleanCaptor.getAllValues().get(0), is(equalTo(true)));
        assertThat(booleanCaptor.getAllValues().get(1), is(equalTo(false)));
    }

    @Test
    public void testConnectErrorEvent() {
        Throwable throwable = new IOException("IOException");
        manager.publishConnectError(throwable);

        verify(listener, only()).onConnectError(throwableCaptor.capture());
        assertThat(throwableCaptor.getValue(), is(equalTo(throwable)));
    }

    @Test
    public void testShouldNotEmitEventsAfterUnregister() {
        manager.unRegister(listener);
        manager.publishConnect("sessionId");
        manager.publishDisconnect(true);
        manager.publishConnectError(new IOException("ioexception"));

        verifyZeroInteractions(listener);
    }

    @Test
    public void testShouldEmitToMultipleListeners() {
        ConnectListener anotherListener = Mockito.mock(ConnectListener.class);
        manager.register(anotherListener);

        manager.publishConnect("sessionId");

        verify(listener, times(1)).onConnect(stringCaptor.capture());
        assertThat(stringCaptor.getValue(), is(equalTo("sessionId")));

        verify(anotherListener, times(1)).onConnect(stringCaptor.capture());
        assertThat(stringCaptor.getValue(), is(equalTo("sessionId")));

        manager.unRegister(anotherListener);

        manager.publishDisconnect(true);
        verify(listener, times(1)).onDisconnect(booleanCaptor.capture());
        assertThat(booleanCaptor.getValue(), is(equalTo(true)));
        verifyNoMoreInteractions(anotherListener);
    }

    @Test
    public void testShouldNotDuplicateListeners() {
        // try to register listener again, but should emit only once for a given listener
        manager.register(listener);
        manager.publishConnect("sessionId");

        verify(listener, only()).onConnect(stringCaptor.capture());
        String id = stringCaptor.getValue();
        assertThat(id, is(equalTo("sessionId")));
    }

    @After
    public void shutdown() {
        manager.unRegister(listener);
    }
}
