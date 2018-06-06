package com.anamika.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.lang.invoke.MethodHandles;

/**
 * {@link ErrorMessagesConfig} creates a {@link MessageSource}
 * It is used to convert the error codes from Validation Exception & convert it to localised error messages.
 * This makes it convenient to change the error messages and also localize the error messages.
 */
@Configuration
public class ErrorMessagesConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
