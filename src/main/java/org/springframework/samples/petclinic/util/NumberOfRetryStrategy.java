package org.springframework.samples.petclinic.util;

import java.util.Map;

public class NumberOfRetryStrategy implements CircuitBreaker.OpenStrategy, CircuitBreaker.CloseStrategy {
    private final int maxRetry;

    public NumberOfRetryStrategy(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public boolean shouldOpen(Map<Object, Object> context) {
        return shouldRetry(context);
    }

    @Override
    public boolean shouldClose(Map<Object, Object> context) {
        return shouldRetry(context);
    }

    private boolean shouldRetry(Map<Object, Object> context) {
        Integer aTry = (Integer) context.get("try");
        if (aTry == null) {
            aTry = 0;
        }
        context.put("try", ++aTry);
        return aTry > maxRetry;
    }
}
