/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.autoconfigure;

import org.aopalliance.aop.Advice;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
@AutoConfigureBefore(ValidationAutoConfiguration.class)
public class ValidationConfiguration {

    @Bean
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
        final MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor() {
            @Override
            protected Advice createMethodValidationAdvice(Validator validator) {
                final OrderedMethodValidationInterceptor orderedMethodValidationInterceptor = new OrderedMethodValidationInterceptor(validator());
                return orderedMethodValidationInterceptor;
            }
        };
        methodValidationPostProcessor.setProxyTargetClass(true);
        return methodValidationPostProcessor;
    }
}
