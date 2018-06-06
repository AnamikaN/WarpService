package com.anamika.app.web;

import com.anamika.app.error.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.Locale;

/**
 *
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageSource messageSource;

    @Autowired
    public DefaultExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private MessageDTO processRequestError(@NotNull String errorCode) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage(errorCode, null, currentLocale);
        return MessageDTO.builder().message(msg).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public MessageDTO processValidationError(Exception ex) {
        LOGGER.error("Unhandled Exception!!", ex);
        Locale currentLocale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage("error.unhandled.exception", null, currentLocale);
        return MessageDTO.builder().message(msg).build();
    }
}
