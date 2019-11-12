package com.github.flycat.context.invoker;

public interface ContextMethodInvoker {
    Object invoke(String methodInfo, String args) throws Exception;
}
