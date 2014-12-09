package org.springframework.samples.petclinic.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NumberOfRetryStrategyTest {
    NumberOfRetryStrategy numberOfRetryStrategy;

    @Before
    public void setUp() throws Exception {
        numberOfRetryStrategy = new NumberOfRetryStrategy(1);
    }

    @Test
    public void should_not_open_when_not_enough_retry() throws Exception {
        assertFalse(numberOfRetryStrategy.shouldOpen(newHashMap()));
    }

    @Test
    public void should_open_when_enough_retry() throws Exception {
        HashMap<Object, Object> context = newHashMap();
        assertFalse(numberOfRetryStrategy.shouldOpen(context));
        assertTrue(numberOfRetryStrategy.shouldOpen(context));
    }

    @Test
    public void should_not_close_when_not_enough_retry() throws Exception {
        assertFalse(numberOfRetryStrategy.shouldClose(newHashMap()));
    }

    @Test
    public void should_close_when_enough_retry() throws Exception {
        HashMap<Object, Object> context = newHashMap();
        assertFalse(numberOfRetryStrategy.shouldClose(context));
        assertTrue(numberOfRetryStrategy.shouldClose(context));
    }
}
