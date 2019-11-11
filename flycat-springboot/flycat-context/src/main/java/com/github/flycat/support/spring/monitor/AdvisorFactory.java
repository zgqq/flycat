package com.github.flycat.support.spring.monitor;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AdvisorFactory {

    public static DefaultPointcutAdvisor traceAdvisor(String expression, AutoTraceInterceptor interceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    public static DefaultPointcutAdvisor traceAdvisor(String expression) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return new DefaultPointcutAdvisor(pointcut, new AutoTraceInterceptor());
    }
}
