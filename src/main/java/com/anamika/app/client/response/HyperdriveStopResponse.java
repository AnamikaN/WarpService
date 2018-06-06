package com.anamika.app.client.response;

import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 *
 */
@Data
@Builder
public class HyperdriveStopResponse implements HyperdriveResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Double cruiseSpeed;
    private final Integer temperature;
}
