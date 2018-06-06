package com.anamika.app.web;


import com.anamika.app.client.exception.PreconditionFailedException;
import com.anamika.app.client.exception.ServiceUnavailableException;
import com.anamika.app.error.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerValidationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageSource messageSource;

    @Autowired
    public ControllerValidationHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private MessageDTO processRequestError(@NotNull String errorCode) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage(errorCode, null, currentLocale);
        return MessageDTO.builder().message(msg).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MessageDTO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        if (error == null) {
            return null;
        }
        return processRequestError(error.getDefaultMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public MessageDTO processServiceUnavailableException(ServiceUnavailableException ex) {
        LOGGER.error("ServiceUnavailableException!!", ex);
        return processRequestError("error.service.unavailable");
    }

    @ExceptionHandler(PreconditionFailedException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public MessageDTO processPreconditionFailedException(PreconditionFailedException ex) {
        LOGGER.error("PreconditionFailedException!!", ex);
        return processRequestError("error.precondition.failed");
    }
}
