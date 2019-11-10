package com.github.flycat.agent.monitor.command;

import com.github.flycat.agent.monitor.AdviceListener;
import com.github.flycat.agent.monitor.session.Session;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The command process provides interaction with the process of the command.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface CommandProcess{

    /**
     * Write some text to the standard output.
     *
     * @param data the text
     * @return a reference to this, so the API can be used fluently
     */
    CommandProcess write(String data);


    /**
     * End the process with the exit status {@literal 0}
     */
    void end();

    /**
     * End the process.
     *
     * @param status the exit status.
     */
    void end(int status);


    /**
     * Register listener
     *
     * @param lock the lock for enhance class
     * @param listener
     */
    void register(int lock, AdviceListener listener);

    /**
     * Unregister listener
     */
    void unregister();

    /**
     * Execution times
     *
     * @return execution times
     */
    AtomicInteger times();

    /**
     * Resume process
     */
    void resume();

    /**
     * Suspend process
     */
    void suspend();

    /**
     * @return the shell session
     */
    Session session();

}
