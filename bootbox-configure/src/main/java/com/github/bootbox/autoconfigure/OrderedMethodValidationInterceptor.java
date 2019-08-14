package com.github.bootbox.autoconfigure;

import org.springframework.core.Ordered;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;

import javax.validation.Validator;

public class OrderedMethodValidationInterceptor extends MethodValidationInterceptor implements Ordered {

    public OrderedMethodValidationInterceptor() {
        super();
    }

    public OrderedMethodValidationInterceptor(Validator validator) {
        super(validator);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
