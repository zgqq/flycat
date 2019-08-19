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
package com.github.flycat.web.spring;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.flycat.context.ContextUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;

public class CustomFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter
        implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        final FastJsonConfig fastJsonConfig = new FastJsonConfig();
        final ArrayList<SerializerFeature> serializerFeatures = Lists.newArrayList();
        serializerFeatures.add(SerializerFeature.QuoteFieldNames);
        if (ContextUtils.isTestProfile()) {
            serializerFeatures.add(SerializerFeature.PrettyFormat);
        }
        fastJsonConfig.setSerializerFeatures(serializerFeatures.toArray(new SerializerFeature[]{}));
        setFastJsonConfig(fastJsonConfig);
    }
}
