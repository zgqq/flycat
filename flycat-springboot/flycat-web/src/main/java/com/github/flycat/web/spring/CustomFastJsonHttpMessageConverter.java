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
