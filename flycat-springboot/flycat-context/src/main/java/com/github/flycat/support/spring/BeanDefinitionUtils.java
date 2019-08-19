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
package com.github.flycat.support.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.function.Supplier;

public class BeanDefinitionUtils {

    public static <T> GenericBeanDefinition register(BeanDefinitionRegistry registry,
                                                     Class<T> clazz,
                                                     String name,
                                                     Supplier<T> supplier) {
        final GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(clazz);
        genericBeanDefinition.setInstanceSupplier(supplier);
        registry.registerBeanDefinition(name, genericBeanDefinition);
        return genericBeanDefinition;
    }
}
