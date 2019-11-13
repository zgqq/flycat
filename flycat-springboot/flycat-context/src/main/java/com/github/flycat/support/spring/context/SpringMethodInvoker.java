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
package com.github.flycat.support.spring.context;

import com.github.flycat.context.invoker.ContextMethodInvoker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;

@Component
public class SpringMethodInvoker implements ContextMethodInvoker, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object invoke(String methodInfo, String args) throws Exception {
        String[] split = methodInfo.split("#");
        String className = split[0];
        String methodName = split[1];
        Class<?> requiredType = Class.forName(className);
        Object bean = applicationContext.getBean(requiredType);

        String[] split1 = args.split(",");
        Method[] methods = requiredType.getMethods();

        Object[] methodArgs = null;
        Method invokeMethod = null;
        for (Method method : methods) {
            String name = method.getName();
            if (name.equals(methodName)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == split1.length) {
                    methodArgs = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        String arg = split1[i];
                        Class<?> parameterType = parameterTypes[i];
                        PropertyEditor editor = PropertyEditorManager.findEditor(parameterType);
                        editor.setAsText(arg);
                        Object value = editor.getValue();
                        methodArgs[i] = value;
                    }
                    invokeMethod = method;
                    break;
                }
            }
        }

        if (invokeMethod == null) {
            throw new NoSuchMethodException("Not found such method " + methodInfo);
        }
        return invokeMethod.invoke(bean, methodArgs);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
