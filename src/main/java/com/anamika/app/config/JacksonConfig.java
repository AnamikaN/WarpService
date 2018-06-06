package com.anamika.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link JacksonConfig} overrides the default {@link ObjectMapper} with a custom {@link ObjectMapper}
 * The custom {@link ObjectMapper} is mainly for {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy}
 * e.g. Field abcXyx get serialized as abc_xyz & vice-versa for de-serialization.
 */
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setDateFormat(new ISO8601DateFormat()) //Best practise e.g. if we start sending timestamp in response
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE) //Needed for Snake Case
                .registerModule(new Jdk8Module()); //Needed for Java 8 Optional
        return objectMapper;
    }
}
