package org.springframework.samples.petclinic.util;

public class CircuitBreakerOpenException extends RuntimeException {
    CircuitBreakerOpenException() {}

    CircuitBreakerOpenException(String message) {
        super(message);
    }

    CircuitBreakerOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    CircuitBreakerOpenException(Throwable cause) {
        super(cause);
    }

    CircuitBreakerOpenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
