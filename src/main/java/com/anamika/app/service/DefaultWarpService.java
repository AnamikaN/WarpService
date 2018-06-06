package com.anamika.app.service;

import com.anamika.app.client.FleetNetClient;
import com.anamika.app.client.HyperdriveClient;
import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.exception.PreconditionFailedException;
import com.anamika.app.client.exception.ServiceUnavailableException;
import com.anamika.app.client.request.HyperdriveStartRequest;
import com.anamika.app.client.response.HyperdriveStartResponse;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 *
 */
@Service
public class DefaultWarpService implements WarpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FleetNetClient fleetNetClient;
    private final HyperdriveClient hyperdriveClient;
    private final MissionService missionService;

    @Autowired
    public DefaultWarpService(FleetNetClient fleetNetClient, HyperdriveClient hyperdriveClient, MissionService missionService) {
        this.fleetNetClient = fleetNetClient;
        this.hyperdriveClient = hyperdriveClient;
        this.missionService = missionService;
    }

    //Step 2
    private void verifyEachVehiclesPreConditions(@NotNull final String vehicleId) {
        if (StringUtils.isEmpty(vehicleId)) {
            return;
        }
        HyperdriveStatusResponse status = null;
        try {
            status = hyperdriveClient.status(vehicleId);
        } catch (ApiException e) {
            throw new ServiceUnavailableException();
        }
        if (!status.isVehicleAtCruiseSpeed()) {
            throw new PreconditionFailedException();
        }
        if (status.getTemperature() > 1000) {
            throw new PreconditionFailedException();
        }
    }

    private void startEachVehiclesHyperdrive(@NotNull final String vehicleId, @NotNull final HyperdriveStartRequest startRequest) {
        if (StringUtils.isEmpty(vehicleId)) {
            return;
        }

        HyperdriveStartResponse startStatus = null;
        try {
            startStatus = hyperdriveClient.start(vehicleId, startRequest);
        } catch (ApiException | JsonProcessingException e) {
            throw new ServiceUnavailableException();
        }
    }


    @Override
    public void scheduleFleet(@NotNull final String fleetId, @NotNull final Integer duration, @NotNull final Double warpSpeed) {
        Preconditions.checkArgument(!StringUtils.isEmpty(fleetId), "Input Fleet Id cannot be null or empty!");
        Preconditions.checkNotNull(duration, "Input Duration cannot be null!");
        Preconditions.checkNotNull(warpSpeed, "Input Warp Speed cannot be null!");

        List<String> vehicleIds = null;
        try {
            vehicleIds = fleetNetClient.getVehiclesInAFleet(fleetId);
        } catch (ApiException e) {
            throw new ServiceUnavailableException();
        }
        if (CollectionUtils.isEmpty(vehicleIds)) {
            return;
        }
        for (String currentVehicleId : vehicleIds) {
            verifyEachVehiclesPreConditions(currentVehicleId);
        }

        HyperdriveStartRequest startRequest = HyperdriveStartRequest.builder()
                .warpSpeed(warpSpeed).duration(duration).build();

        for (String currentVehicleId : vehicleIds) {
            startEachVehiclesHyperdrive(currentVehicleId, startRequest);
        }
        missionService.scheduleNewMission(fleetId, duration, warpSpeed);
    }
}
