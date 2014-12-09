package org.springframework.samples.petclinic.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CircuitBreakerTest {
    @Rule public ExpectedException thrown = ExpectedException.none();

    interface TargetRemoteSystem {
        Object doSomeDangerousStuff() throws TargetSystemException;
    }

    class TargetSystemException extends Exception {
        public TargetSystemException(String message) { super(message); }
    }

    @Mock private TargetRemoteSystem dangerousTarget;

    @Mock private CircuitBreaker.OpenStrategy openStrategy;

    @Mock private CircuitBreaker.CloseStrategy closeStrategy;

    private TargetRemoteSystem breakableTarget;

    @Before
    public void setUp() throws Exception {
        when(dangerousTarget.doSomeDangerousStuff())
            .thenThrow(new TargetSystemException("Fake Exception!"));
        when(openStrategy.shouldOpen(anyMapOf(Object.class, Object.class))).thenReturn(true);
        when(closeStrategy.shouldClose(anyMapOf(Object.class, Object.class))).thenReturn(true);
        CircuitBreaker cb = new CircuitBreaker(openStrategy, closeStrategy);
        breakableTarget = (TargetRemoteSystem) cb.wraps(dangerousTarget, TargetRemoteSystem.class);
    }

    @Test
    public void breakable_target_call_dangerous_target() throws Exception {
        reset(dangerousTarget);
        breakableTarget.doSomeDangerousStuff();
        verify(dangerousTarget).doSomeDangerousStuff();
    }

    @Test
    public void breakable_target_throws_dangerous_target_exception() throws Exception {
        thrown.expect(TargetSystemException.class);
        thrown.expectMessage("Fake Exception!");
        breakableTarget.doSomeDangerousStuff();
    }

    @Test
    public void breakable_target_throws_circuit_breaker_open_exception_on_second_try() throws Exception {
        try{breakableTarget.doSomeDangerousStuff();} catch (Exception ignored){}
        thrown.expect(CircuitBreakerOpenException.class);
        thrown.expectMessage("Circuit broken!");
        breakableTarget.doSomeDangerousStuff();
    }

    @Test
    public void breakable_target_throws_target_exception_on_third_try() throws Exception {
        try{breakableTarget.doSomeDangerousStuff();} catch (Exception ignored){}
        try{breakableTarget.doSomeDangerousStuff();} catch (Exception ignored){}
        thrown.expect(TargetSystemException.class);
        thrown.expectMessage("Fake Exception!");
        breakableTarget.doSomeDangerousStuff();
    }

    @Test
    public void breakable_close_after_success_retry() throws Exception {
        try{breakableTarget.doSomeDangerousStuff();fail("Should throw an Exception");} catch (Exception ignored){}
        try{breakableTarget.doSomeDangerousStuff();fail("Should throw an Exception");} catch (Exception ignored){}
        reset(dangerousTarget);
        breakableTarget.doSomeDangerousStuff();
        verify(dangerousTarget).doSomeDangerousStuff();
        verify(openStrategy).shouldOpen(anyMapOf(Object.class, Object.class));
        verify(closeStrategy).shouldClose(anyMapOf(Object.class, Object.class));
    }
}
