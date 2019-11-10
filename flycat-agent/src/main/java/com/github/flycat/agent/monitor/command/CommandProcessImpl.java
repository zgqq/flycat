package com.github.flycat.agent.monitor.command;

import com.github.flycat.agent.monitor.AdviceListener;
import com.github.flycat.agent.monitor.AdviceWeaver;
import com.github.flycat.agent.monitor.session.Session;

import java.util.concurrent.atomic.AtomicInteger;

public class CommandProcessImpl implements CommandProcess {

    private int enhanceLock = -1;
    private AtomicInteger times = new AtomicInteger();
    private AdviceListener suspendedListener = null;
    private final Session session;

    public CommandProcessImpl(Session session) {
        this.session = session;
    }

    @Override
    public AtomicInteger times() {
        return times;
    }


    @Override
    public CommandProcess write(String data) {
        return this;
    }

    @Override
    public void register(int enhanceLock, AdviceListener listener) {
        this.enhanceLock = enhanceLock;
        AdviceWeaver.reg(enhanceLock, listener);
    }

    @Override
    public void unregister() {
        AdviceWeaver.unReg(enhanceLock);
    }

    @Override
    public void resume() {
        if (this.enhanceLock >= 0 && suspendedListener != null) {
            AdviceWeaver.resume(enhanceLock, suspendedListener);
            suspendedListener = null;
        }
    }

    @Override
    public void suspend() {
        if (this.enhanceLock >= 0) {
            suspendedListener = AdviceWeaver.suspend(enhanceLock);
        }
    }

    @Override
    public Session session() {
        return getSession();
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void end() {
        end(0);
    }

    @Override
    public void end(int statusCode) {
    }
}
