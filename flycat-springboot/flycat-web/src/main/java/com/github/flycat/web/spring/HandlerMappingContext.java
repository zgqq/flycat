package com.github.flycat.web.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.servlet.DispatcherServlet.REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME;

@Component
public class HandlerMappingContext implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerMappingContext.class);

    /**
     * List of HandlerMappings used by this servlet.
     */
    @Nullable
    private List<HandlerMapping> handlerMappings;

    private RequestToViewNameTranslator viewNameTranslator;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final ApplicationContext context = event.getApplicationContext();
        Map<String, HandlerMapping> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.handlerMappings = new ArrayList<>(matchingBeans.values());
            // We keep HandlerMappings in sorted order.
            AnnotationAwareOrderComparator.sort(this.handlerMappings);
        }

        try {
            this.viewNameTranslator =
                    context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
        } catch (NoSuchBeanDefinitionException e) {
            this.viewNameTranslator = new DefaultRequestToViewNameTranslator();
        }
    }

    /**
     * Return the HandlerExecutionChain for this request.
     * <p>Tries all handler mappings in order.
     *
     * @param request current HTTP request
     * @return the HandlerExecutionChain, or {@code null} if no handler could be found
     */
    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) {
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerExecutionChain handler = null;
                try {
                    handler = mapping.getHandler(request);
                } catch (Exception e) {
                    LOGGER.error("Unable to get handler", e);
                }
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    public boolean isResponseBody(HttpServletRequest request) {
        final HandlerExecutionChain handler = getHandler(request);
        boolean responseBody = false;
        if (handler != null) {
            HandlerMethod handlerHandler = (HandlerMethod) handler.getHandler();
            final MethodParameter returnType = handlerHandler.getReturnType();
            responseBody = (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ResponseBody.class) ||
                    returnType.hasMethodAnnotation(ResponseBody.class));
        }
        return responseBody;
    }

    public RequestToViewNameTranslator getViewNameTranslator() {
        return viewNameTranslator;
    }


    public String getViewName(HttpServletRequest request) {
        try {
            return this.viewNameTranslator.getViewName(request);
        } catch (Exception e) {
            LOGGER.error("Unable to get view name", e);
            return "error";
        }
    }
}
