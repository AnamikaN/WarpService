package com.anamika.app.client.exception;

import com.anamika.app.error.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;

/**
 *
 */
@Data
@Builder
public class ApiException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final long serialVersionUID = 6567674085677367542L;

    private final HttpStatus responseStatus;
    private final String errorMessage;

    public static ApiException getApiErrorMessage(ObjectMapper objectMapper, HttpStatus responseStatus, InputStream stream) {
        try {
            String errorString = new String(ByteStreams.toByteArray(stream));
            MessageDTO messageDTO = objectMapper.readValue(errorString, MessageDTO.class);
            return ApiException.builder().responseStatus(responseStatus).errorMessage(messageDTO.getMessage()).build();
        } catch (JsonProcessingException e) {
            return ApiException.builder().responseStatus(responseStatus).errorMessage("JsonProcessingException").build();
        } catch (IOException e) {
            return ApiException.builder().responseStatus(responseStatus).errorMessage("IOException").build();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                LOGGER.warn("IOException trying to close stream!!", e);
            }
        }
    }

}
