package org.springframework.samples.petclinic.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import static com.google.common.base.Preconditions.checkNotNull;

public class CircuitBreakerFactoryBean<T> extends AbstractFactoryBean<T> implements InitializingBean {
    Class<T> type;
    T target;
    CircuitBreaker.CloseStrategy closeStrategy;
    CircuitBreaker.OpenStrategy openStrategy;
    CircuitBreaker circuitBreaker;
    @Override
    public void afterPropertiesSet() throws Exception {
        checkNotNull(type);
        checkNotNull(target);
        checkNotNull(closeStrategy);
        checkNotNull(openStrategy);
        circuitBreaker = new CircuitBreaker(openStrategy, closeStrategy);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T createInstance() throws Exception {
        return (T) circuitBreaker.wraps(target, type);
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public void setCloseStrategy(CircuitBreaker.CloseStrategy closeStrategy) {
        this.closeStrategy = closeStrategy;
    }

    public void setOpenStrategy(CircuitBreaker.OpenStrategy openStrategy) {
        this.openStrategy = openStrategy;
    }
}
