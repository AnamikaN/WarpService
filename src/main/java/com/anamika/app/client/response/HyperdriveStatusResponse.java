package com.anamika.app.client.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class HyperdriveStatusResponse implements HyperdriveResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Double cruiseSpeed;
    private final Double warpSpeed;
    private final Integer duration;
    private final Integer temperature;

    @JsonIgnore
    public boolean isVehicleInHyperdrive() {
        return (warpSpeed != null);
    }

    @JsonIgnore
    public boolean isVehicleAtCruiseSpeed() {
        return (cruiseSpeed != null);
    }
}
