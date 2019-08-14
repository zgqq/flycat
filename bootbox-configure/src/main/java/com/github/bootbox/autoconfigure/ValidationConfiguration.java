package com.github.bootbox.autoconfigure;

import org.aopalliance.aop.Advice;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
public class ValidationConfiguration {

    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "false")
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor() {
            @Override
            protected Advice createMethodValidationAdvice(javax.validation.Validator validator) {
                return new OrderedMethodValidationInterceptor(validator());
            }
        };
    }
}
