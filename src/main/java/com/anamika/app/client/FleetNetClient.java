package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 */
public interface FleetNetClient {
    List<String> getVehiclesInAFleet(@NotNull final String fleetId) throws ApiException;
}
