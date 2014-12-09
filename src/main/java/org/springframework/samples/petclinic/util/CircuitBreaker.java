package org.springframework.samples.petclinic.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * <b>Circuit Breaker</b> implementation that cant proxy dangerous components.
 *
 * @see <a href="http://martinfowler.com/bliki/CircuitBreaker.html">Circuit Breaker</a>
 */
public class CircuitBreaker {
    /**
     * Circuit breaker open strategy used to define when to get in open state.
     */
    interface OpenStrategy {
        /**
         * Method called by {@link org.springframework.samples.petclinic.util.CircuitBreaker} in closed state each time
         * an error occurs during the target component call.
         *
         * @param context Context provided to this implementation to make the decision.
         * @return Return <code>true</code> if the {@link org.springframework.samples.petclinic.util.CircuitBreaker}
         * should get in open state, <code>false</code> otherwise.
         */
        boolean shouldOpen(Map<Object, Object> context);
    }

    /**
     * Circuit breaker closing strategy used to define when to get out of open state.
     */
    interface CloseStrategy {
        /**
         * Method called by {@link fr.gwallet.tools.connection.CircuitBreaker} in open state each time
         * an attempt is made to call the target component.
         *
         * @param context Context provided to this implementation to make the decision.
         * @return Return <code>true</code> if the {@link fr.gwallet.tools.connection.CircuitBreaker}
         * should try to get back in closed state, <code>false</code> otherwise.
         */
        boolean shouldClose(Map<Object, Object> context);
    }

    private final OpenStrategy openStrategy;

    private final CloseStrategy closeStrategy;

    public CircuitBreaker(OpenStrategy openStrategy, CloseStrategy closeStrategy) {
        checkNotNull(openStrategy);
        checkNotNull(closeStrategy);
        this.openStrategy = openStrategy;
        this.closeStrategy = closeStrategy;
    }

    public Object wraps(final Object target, final Class<?>... ofTypes) {
        return newProxyInstance(target.getClass().getClassLoader(), ofTypes, new InvocationHandler() {
            State state = new ClosedState();
            Map<Object, Object> context = newHashMap();
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return state.forward(target, method, args);
            }

            abstract class State {
                abstract Object forward(Object target, Method method, Object... args) throws Throwable;
            }

            class ClosedState extends State {
                @Override
                Object forward(Object target, Method method, Object... args) throws Throwable {
                    try {
                        return method.invoke(target, args);
                    } catch (InvocationTargetException cause) {
                        if (openStrategy.shouldOpen(context)) state = new OpenState();
                        throw cause.getTargetException();
                    }
                }
            }

            class OpenState extends State {
                @Override
                Object forward(Object target, Method method, Object... args) throws Throwable {
                    if (closeStrategy.shouldClose(context)) state = new HalfOpenState();
                    throw new CircuitBreakerOpenException("Circuit broken!");
                }
            }

            class HalfOpenState extends State {
                @Override
                Object forward(Object target, Method method, Object... args) throws Throwable {
                    try {
                        Object result = method.invoke(target, args);
                        state = new ClosedState();
                        return result;
                    } catch (InvocationTargetException cause) {
                        state = new OpenState();
                        throw cause.getTargetException();
                    }
                }
            }
        });
    }
}
