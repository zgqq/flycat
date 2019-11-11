package com.github.flycat.agent.monitor;

import com.github.flycat.agent.monitor.command.CommandProcess;
import com.github.flycat.agent.monitor.command.TraceCommand;
import com.github.flycat.util.StringUtils;

/**
 * @author beiwei30 on 29/11/2016.
 */
public class TraceAdviceListener extends AbstractTraceAdviceListener implements InvokeTraceable {

    /**
     * Constructor
     */
    public TraceAdviceListener(TraceCommand command, CommandProcess process) {
        super(command, process);
    }

    /**
     * trace 会在被观测的方法体中，在每个方法调用前后插入字节码，所以方法调用开始，结束，抛异常的时候，都会回调下面的接口
     */
    @Override
    public void invokeBeforeTracing(String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber)
            throws Throwable {
        threadBoundEntity.get().view.begin(
            StringUtils.normalizeClassName(tracingClassName) + ":" + tracingMethodName + "()" + " #" + tracingLineNumber);
    }

    @Override
    public void invokeAfterTracing(String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber)
            throws Throwable {
        threadBoundEntity.get().view.end();
    }

    @Override
    public void invokeThrowTracing(String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber)
            throws Throwable {
        threadBoundEntity.get().view.end("throws Exception");
    }

}
