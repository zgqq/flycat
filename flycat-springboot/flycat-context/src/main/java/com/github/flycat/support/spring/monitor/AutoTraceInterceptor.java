package com.github.flycat.support.spring.monitor;

import com.github.flycat.agent.monitor.AgentMain;
import com.github.flycat.agent.monitor.command.TraceCommand;
import com.google.common.base.Stopwatch;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AutoTraceInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTraceInterceptor.class);

    private Set<Method> methods = ConcurrentHashMap.newKeySet();

    protected Object invokeUnderTrace(MethodInvocation invocation) throws Throwable {
        Stopwatch started = Stopwatch.createStarted();
        try {
            return invocation.proceed();
        } finally {
            started.stop();
            long elapsed = started.elapsed(TimeUnit.MILLISECONDS);
            if (elapsed > 200) {
                Method method = invocation.getMethod();
                String className = method.getDeclaringClass().getName();
                if (!methods.contains(method)) {
                    synchronized (this) {
                        if (!methods.contains(method)) {
                            TraceCommand traceCommand = new TraceCommand();
                            traceCommand.setClassPattern(className);
                            traceCommand.setMethodPattern(method.getName());
                            traceCommand.setConditionExpress("#cost>100");
                            traceCommand.setRegEx(false);
                            traceCommand.setNumberOfLimit(100);
                            traceCommand.setSkipJDKTrace(true);
                            LOGGER.info("Sending trace command, {}", traceCommand);
                            AgentMain.sendCommand(traceCommand);
                            methods.add(method);
                        }
                    }
                }
                LOGGER.warn("Method " + className + "execution longer than 10 ms!");
            }
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invokeUnderTrace(invocation);
    }
}
