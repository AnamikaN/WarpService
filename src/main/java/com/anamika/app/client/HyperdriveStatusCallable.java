package com.anamika.app.client;

import com.anamika.app.service.ServiceInstanceProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.lang.invoke.MethodHandles;

/**
 *
 */
@Data
@Builder
public class HyperdriveStatusCallable extends HyperdriveCallable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String PATH = "hyperdrive.service/status/";

    private final ObjectMapper objectMapper;
    private final ServiceInstanceProvider instanceProvider;
    private final String vehicleId;
    private final String postData;

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HTTP_METHOD;
    }
}
