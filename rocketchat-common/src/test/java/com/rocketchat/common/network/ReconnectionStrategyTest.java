package com.rocketchat.common.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ReconnectionStrategyTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testMaxAttemptsValue() {
        ReconnectionStrategy strategy = new ReconnectionStrategy(5, 1000);

        assertTrue("max attempts", strategy.getMaxAttempts() == 5);
    }

    @Test
    public void testReconnectionInterval() {
        ReconnectionStrategy strategy = new ReconnectionStrategy(5, 1000);

        assertTrue("reconnect interval", strategy.getReconnectInterval() == 1000);
    }

    @Test
    public void testNumberAttempts() {
        ReconnectionStrategy strategy = new ReconnectionStrategy(5, 1000);

        assertTrue(strategy.getNumberOfAttempts() == 0);

        strategy.processAttempts();
        assertTrue(strategy.getNumberOfAttempts() == 1);
        strategy.processAttempts();
        assertTrue(strategy.getNumberOfAttempts() == 2);
        strategy.processAttempts();
        assertTrue(strategy.getNumberOfAttempts() == 3);
        strategy.processAttempts();
        assertTrue(strategy.getNumberOfAttempts() == 4);

        strategy.setNumberOfAttempts(666);
        assertTrue(strategy.getNumberOfAttempts() == 666);
    }

    @Test
    public void testMaxInterval() {
        ReconnectionStrategy strategy = new ReconnectionStrategy(5, 60000);

        assertTrue(strategy.getReconnectInterval() == 30000);
    }
}
