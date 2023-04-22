package com.github.flycat.spi.impl.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonSerializationConfig implements com.github.flycat.spi.json.SerializationConfig {

    private final ObjectMapper objectMapper;
    private final SerializationConfig serializationConfig;

    public JacksonSerializationConfig(ObjectMapper objectMapper, SerializationConfig serializationConfig) {
        this.objectMapper = objectMapper;
        this.serializationConfig = serializationConfig;
    }


//    @Override
//    public void disableReferencesAsNull() {
////        this.objectMapper.setConfig(this.serializationConfig.without(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
////        );
//    }

    @Override
    public void includeNonNull() {
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void includeNonEmpty() {
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
}
