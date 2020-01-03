package com.github.flycat.agent.monitor;

import com.github.flycat.agent.monitor.command.AnnotatedCommand;
import com.github.flycat.agent.monitor.command.CommandProcess;
import com.github.flycat.agent.monitor.command.CommandProcessImpl;
import com.github.flycat.agent.monitor.command.EnhancerCommand;
import com.github.flycat.agent.monitor.session.Session;
import com.github.flycat.agent.monitor.session.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import static com.github.flycat.agent.monitor.session.Session.INSTRUMENTATION;

public class AgentMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentMain.class);

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    private final static Object lock = new Object();
    private static Session session;

    private static void main(String args, final Instrumentation inst) {
        synchronized (lock) {
            session = new SessionImpl();
            session.put(INSTRUMENTATION, inst);
        }
    }


    private static final String ADVICEWEAVER = "com.github.flycat.agent.monitor.AdviceWeaver";
    private static final String ON_BEFORE = "methodOnBegin";
    private static final String ON_RETURN = "methodOnReturnEnd";
    private static final String ON_THROWS = "methodOnThrowingEnd";
    private static final String BEFORE_INVOKE = "methodOnInvokeBeforeTracing";
    private static final String AFTER_INVOKE = "methodOnInvokeAfterTracing";
    private static final String THROW_INVOKE = "methodOnInvokeThrowTracing";

    static {
        try {
            initSpy(AgentMain.class.getClassLoader());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void initSpy(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        Class<?> adviceWeaverClass = classLoader.loadClass(ADVICEWEAVER);
        Method onBefore = adviceWeaverClass.getMethod(ON_BEFORE, int.class, ClassLoader.class, String.class,
                String.class, String.class, Object.class, Object[].class);
        Method onReturn = adviceWeaverClass.getMethod(ON_RETURN, Object.class);
        Method onThrows = adviceWeaverClass.getMethod(ON_THROWS, Throwable.class);
        Method beforeInvoke = adviceWeaverClass.getMethod(BEFORE_INVOKE, int.class, String.class, String.class, String.class, int.class);
        Method afterInvoke = adviceWeaverClass.getMethod(AFTER_INVOKE, int.class, String.class, String.class, String.class, int.class);
        Method throwInvoke = adviceWeaverClass.getMethod(THROW_INVOKE, int.class, String.class, String.class, String.class, int.class);
        Spy.initForAgentLauncher(classLoader, onBefore, onReturn, onThrows, beforeInvoke, afterInvoke, throwInvoke, null);
    }


    public static void sendCommand(AnnotatedCommand command) {
        synchronized (lock) {
            if (session == null) {
                LOGGER.warn("Unable to handle command, attach agent failed?");
                return;
//                throw new RuntimeException("Not initiated yet");
            }
            CommandProcess commandProcess = new CommandProcessImpl(session);
            command.process(commandProcess);
        }
    }
}
