package com.github.flycat.agent.monitor.command;


import com.github.flycat.agent.monitor.AbstractTraceAdviceListener;
import com.github.flycat.agent.monitor.AdviceListener;
import com.github.flycat.agent.monitor.Enhancer;
import com.github.flycat.agent.monitor.InvokeTraceable;
import com.github.flycat.agent.monitor.matcher.Matcher;
import com.github.flycat.agent.monitor.session.Session;
import com.github.flycat.agent.monitor.util.EnhancerAffect;
import com.github.flycat.agent.monitor.util.LogUtil;
import org.slf4j.Logger;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Collections;
import java.util.List;

/**
 * @author beiwei30 on 29/11/2016.
 */
public abstract class EnhancerCommand extends AnnotatedCommand {

    private static final Logger logger = LogUtil.getArthasLogger();
    protected static final List<String> EMPTY = Collections.emptyList();
    public static final String[] EXPRESS_EXAMPLES = {"params", "returnObj", "throwExp", "target", "clazz", "method",
            "{params,returnObj}", "params[0]"};

    protected Matcher classNameMatcher;
    protected Matcher methodNameMatcher;

    /**
     * 类名匹配
     *
     * @return 获取类名匹配
     */
    protected abstract Matcher getClassNameMatcher();

    /**
     * 方法名匹配
     *
     * @return 获取方法名匹配
     */
    protected abstract Matcher getMethodNameMatcher();

    /**
     * 获取监听器
     *
     * @return 返回监听器
     */
    protected abstract AdviceListener getAdviceListener(CommandProcess process);

    @Override
    public void process(final CommandProcess process) {

        // start to enhance
        enhance(process);
    }


    protected void enhance(CommandProcess process) {
        Session session = process.session();
        if (!session.tryLock()) {
            process.write("someone else is enhancing classes, pls. wait.\n");
            process.end();
            return;
        }
        int lock = session.getLock();
        try {
            Instrumentation inst = session.getInstrumentation();
            AdviceListener listener = getAdviceListener(process);
            if (listener == null) {
                warn(process, "advice listener is null");
                return;
            }
            boolean skipJDKTrace = false;
            if (listener instanceof AbstractTraceAdviceListener) {
                skipJDKTrace = ((AbstractTraceAdviceListener) listener).getCommand().isSkipJDKTrace();
            }

            EnhancerAffect effect = Enhancer.enhance(inst, lock, listener instanceof InvokeTraceable,
                    skipJDKTrace, getClassNameMatcher(), getMethodNameMatcher());

            if (effect.cCnt() == 0 || effect.mCnt() == 0) {
                // no class effected
                // might be method code too large
                process.write("No class or method is affected, try:\n"
                        + "1. sm CLASS_NAME METHOD_NAME to make sure the method you are tracing actually exists (it might be in your parent class).\n"
                        + "2. reset CLASS_NAME and try again, your method body might be too large.\n"
                );
                process.end();
                return;
            }

            // 这里做个补偿,如果在enhance期间,unLock被调用了,则补偿性放弃
            if (session.getLock() == lock) {
                // 注册通知监听器
                process.register(lock, listener);
            }

            process.write(effect + "\n");
        } catch (UnmodifiableClassException e) {
            logger.error("error happens when enhancing class", e);
        } finally {
            if (session.getLock() == lock) {
                // enhance结束后解锁
                process.session().unLock();
            }
        }
    }


    private static void warn(CommandProcess process, String message) {
        logger.error(message);
        process.write("cannot operate the current command, pls. check arthas.log\n");
    }
}
