package com.github.flycat.platform.springboot;

import com.github.flycat.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class CustomTransactionAttributeSource extends AbstractFallbackTransactionAttributeSource implements TransactionAttributeSource {
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
                rbta.setRollbackRules(Lists.newArrayList());
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
