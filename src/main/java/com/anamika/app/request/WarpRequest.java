package com.anamika.app.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

/**
 *
 */
@Data
@Builder(toBuilder = true)
public class WarpRequest {
    @NotNull(message = "error.fleetId.notnull")
    @Size(min = 1, message = "error.fleetId.empty")
    private final String fleetId;
    @NotNull(message = "error.duration.notnull")
    @Min(value = 1, message = "error.duration.size")
    @Max(value = 8760, message = "error.duration.size")
    private final Integer duration;
    @NotNull(message = "error.warpSpeed.notnull")
    @DecimalMin(value = "0.001", message = "error.warpSpeed.size")
    @DecimalMax(value = "1.0", message = "error.warpSpeed.size")
    private final Double warpSpeed;
}
