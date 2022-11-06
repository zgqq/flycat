/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
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
import java.util.concurrent.atomic.AtomicLong;

public class AutoTraceInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTraceInterceptor.class);

    private final Set<Method> methods = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<Method, AtomicLong> timeCounter = new ConcurrentHashMap<>();

    private final int elapsedThreshold;
    private final int exceedTimes;

    public AutoTraceInterceptor(int elapsedThreshold, int exceedTimes) {
        this.elapsedThreshold = elapsedThreshold;
        this.exceedTimes = exceedTimes;
    }

    public AutoTraceInterceptor() {
        this(100, 2);
    }


    protected Object invokeUnderTrace(MethodInvocation invocation) throws Throwable {
        Stopwatch started = Stopwatch.createStarted();
        try {
            return invocation.proceed();
        } finally {
            started.stop();
            long elapsed = started.elapsed(TimeUnit.MILLISECONDS);
            int threshold = this.elapsedThreshold;
            if (elapsed > threshold) {
                Method method = invocation.getMethod();
                String className = method.getDeclaringClass().getName();
                AtomicLong initValue = new AtomicLong(1);
                AtomicLong atomicLong = timeCounter.putIfAbsent(method, initValue);
                if (atomicLong != null) {
                    atomicLong.incrementAndGet();
                } else {
                    atomicLong = initValue;
                }
                int times = this.exceedTimes;
                if (atomicLong != null && atomicLong.get() > times) {
                    if (!methods.contains(method)) {
                        synchronized (this) {
                            if (!methods.contains(method)) {
                                TraceCommand traceCommand = new TraceCommand();
                                traceCommand.setClassPattern(className);
                                traceCommand.setMethodPattern(method.getName());
                                traceCommand.setConditionExpress("#cost>100");
                                traceCommand.setRegEx(false);
                                traceCommand.setNumberOfLimit(Integer.MAX_VALUE);
                                traceCommand.setSkipJDKTrace(true);
                                LOGGER.info("Sending trace command, {}", traceCommand);
                                AgentMain.sendCommand(traceCommand);
                                methods.add(method);
                            }
                        }
                    }
                }
                LOGGER.warn("Method (" + className + "#" + method.getName() + ") execution " +
                        "cost " + elapsed + " ms, greater than " + (atomicLong.get() - 1) + " times ");
            }
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invokeUnderTrace(invocation);
    }
}
*/
