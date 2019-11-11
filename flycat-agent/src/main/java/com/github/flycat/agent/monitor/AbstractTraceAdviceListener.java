package com.github.flycat.agent.monitor;

import com.github.flycat.agent.monitor.command.CommandProcess;
import com.github.flycat.agent.monitor.command.TraceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ralf0131 2017-01-06 16:02.
 */
public class AbstractTraceAdviceListener extends ReflectAdviceListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTraceAdviceListener.class);

    protected final ThreadLocalWatch threadLocalWatch = new ThreadLocalWatch();
    protected TraceCommand command;
    protected CommandProcess process;

    protected final ThreadLocal<TraceEntity> threadBoundEntity = new ThreadLocal<TraceEntity>() {

        @Override
        protected TraceEntity initialValue() {
            return new TraceEntity();
        }
    };

    /**
     * Constructor
     */
    public AbstractTraceAdviceListener(TraceCommand command, CommandProcess process) {
        this.command = command;
        this.process = process;
    }

    @Override
    public void destroy() {
        threadBoundEntity.remove();
    }

    @Override
    public void before(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args)
            throws Throwable {
        threadBoundEntity.get().view.begin(clazz.getName() + ":" + method.getName() + "()");
        threadBoundEntity.get().deep++;
        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args,
                               Object returnObject) throws Throwable {
        threadBoundEntity.get().view.end();
        final Advice advice = Advice.newForAfterRetuning(loader, clazz, method, target, args, returnObject);
        finishing(advice);
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args,
                              Throwable throwable) throws Throwable {
        int lineNumber = throwable.getStackTrace()[0].getLineNumber();
        threadBoundEntity.get().view.begin("throw:" + throwable.getClass().getName() + "()" + " #" + lineNumber).end().end();
        final Advice advice = Advice.newForAfterThrowing(loader, clazz, method, target, args, throwable);
        finishing(advice);
    }

    public TraceCommand getCommand() {
        return command;
    }

    private void finishing(Advice advice) {
        // 本次调用的耗时
        double cost = threadLocalWatch.costInMillis();
        if (--threadBoundEntity.get().deep == 0) {
            try {
                if (isConditionMet(command.getConditionExpress(), advice, cost)) {
                    // 满足输出条件
                    if (isLimitExceeded(command.getNumberOfLimit(), process.times().get())) {
                        // TODO: concurrency issue to abort process
                        abortProcess(process, command.getNumberOfLimit());
                    } else {
                        process.times().incrementAndGet();
                        // TODO: concurrency issues for process.write
                        process.write(threadBoundEntity.get().view.draw() + "\n");
                    }
                }
            } catch (Throwable e) {
                LOGGER.warn("trace failed.", e);
                process.write("trace failed, condition is: " + command.getConditionExpress() + ", " + e.getMessage());
                process.end();
            } finally {
                threadBoundEntity.remove();
            }
        }
    }
}
