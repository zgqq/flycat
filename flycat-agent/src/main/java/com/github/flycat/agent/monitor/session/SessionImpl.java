package com.github.flycat.agent.monitor.session;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SessionImpl implements Session {
    private static final AtomicInteger LOCK_SEQUENCE = new AtomicInteger();
    private static final int LOCK_TX_EMPTY = -1;
    private final AtomicInteger lock = new AtomicInteger(LOCK_TX_EMPTY);

    private Map<String, Object> data = new HashMap<String, Object>();

    @Override
    public Session put(String key, Object obj) {
        if (obj == null) {
            data.remove(key);
        } else {
            data.put(key, obj);
        }
        return this;
    }

    @Override
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    @Override
    public <T> T remove(String key) {
        return (T) data.remove(key);
    }

    @Override
    public boolean tryLock() {
        return lock.compareAndSet(LOCK_TX_EMPTY, LOCK_SEQUENCE.getAndIncrement());
    }

    @Override
    public void unLock() {
        int currentLockTx = lock.get();
        if (!lock.compareAndSet(currentLockTx, LOCK_TX_EMPTY)) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isLocked() {
        return lock.get() != LOCK_TX_EMPTY;
    }

    @Override
    public int getLock() {
        return lock.get();
    }

    @Override
    public String getSessionId() {
        return (String) data.get(ID);
    }


    @Override
    public Instrumentation getInstrumentation() {
        return (Instrumentation) data.get(INSTRUMENTATION);
    }
}

