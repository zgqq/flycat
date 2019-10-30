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
package com.github.flycat.platform.springboot;

import com.github.flycat.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CustomTransactionAttributeSource extends AbstractFallbackTransactionAttributeSource
        implements TransactionAttributeSource {
    private final CustomTransactionParser annotationParser;

    public CustomTransactionAttributeSource() {
        annotationParser = new CustomTransactionParser();
    }

    @Override
    public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        final TransactionAttribute transactionAttribute1 = super.getTransactionAttribute(method, targetClass);
        if (transactionAttribute1 != null) {
            return transactionAttribute1;
        }
        return super.getTransactionAttribute(method, targetClass);
    }

    @Override
    protected TransactionAttribute findTransactionAttribute(Class<?> clazz) {
        TransactionAttribute attr = annotationParser.parseTransactionAnnotation(clazz);
        return attr;
    }

    @Override
    protected TransactionAttribute findTransactionAttribute(Method method) {
        TransactionAttribute attr = annotationParser.parseTransactionAnnotation(method);
        return attr;
    }


    public static class CustomTransactionParser extends SpringTransactionAnnotationParser {

        @Override
        public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
            AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
                    element, Transactional.class, false, false);
            if (attributes != null) {
                RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
                final ArrayList<RollbackRuleAttribute> rollbackRules = Lists.newArrayList();
                for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
                    rollbackRules.add(new RollbackRuleAttribute(rbRule));
                }
                rbta.setRollbackRules(rollbackRules);

                rbta.setQualifier(attributes.getString("value"));
                rbta.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);


                rbta.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
                rbta.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);
                rbta.setReadOnly(false);
                return rbta;
            } else {
                return super.parseTransactionAnnotation(element);
            }
        }
    }
}
