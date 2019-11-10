package com.github.flycat.agent.monitor;

import com.github.flycat.agent.monitor.command.CommandProcess;
import com.github.flycat.agent.monitor.command.CommandProcessImpl;
import com.github.flycat.agent.monitor.command.EnhancerCommand;
import com.github.flycat.agent.monitor.session.Session;
import com.github.flycat.agent.monitor.session.SessionImpl;

import java.lang.instrument.Instrumentation;

import static com.github.flycat.agent.monitor.session.Session.INSTRUMENTATION;

public class AgentMain {

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    private final static Object lock = new Object();
    private static CommandProcess commandProcess;

    private static void main(String args, final Instrumentation inst) {
        synchronized (lock) {
            Session session = new SessionImpl();
            session.put(INSTRUMENTATION, inst);
            commandProcess = new CommandProcessImpl(session);
        }
    }

    public static void sendCommand(EnhancerCommand command) {
        synchronized (lock) {
            if (commandProcess == null) {
                throw new RuntimeException("Not initiated yet");
            }
            command.process(commandProcess);
        }
    }
}
