package com.anamika.app.service;

import com.anamika.app.client.FleetNetClient;
import com.anamika.app.client.HyperdriveClient;
import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.request.HyperdriveStopRequest;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.anamika.app.client.response.HyperdriveStopResponse;
import com.anamika.app.model.Fleet;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
public class MissionMonitoringService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Integer SAFE_TEMPERATURE_MAX = 1000;
    private static final Double DEFAULT_CRUISE_SPEED = 0.00089; //ToDo: This is an assumption!
    private final FleetNetClient fleetNetClient;
    private final HyperdriveClient hyperdriveClient;
    private final MissionService missionService;

    @Autowired
    public MissionMonitoringService(FleetNetClient fleetNetClient, HyperdriveClient hyperdriveClient, MissionService missionService) {
        this.fleetNetClient = fleetNetClient;
        this.hyperdriveClient = hyperdriveClient;
        this.missionService = missionService;
    }


    private boolean isCurrentVehiclesTemperatureHigh(String vehicleId) {
        HyperdriveStatusResponse status = null;
        try {
            status = hyperdriveClient.status(vehicleId);
        } catch (ApiException e) {
            LOGGER.error("Error fetching hyperdrive status of vehicle {}! Assuming vehicle is temperature is safe!", vehicleId, e);
            //ToDo: This is an assumption!
            return false;
        }
        if (status.getTemperature() == null) {
            LOGGER.warn("For vehicle id {}, the hyperdrive status {} does not have temperature! Assuming vehicle is temperature is safe!", vehicleId, status);
            //ToDo: This is an assumption!
            return false;
        }
        return (status.getTemperature() > SAFE_TEMPERATURE_MAX);
    }

    private boolean isAnyVehiclesTemperatureHigh(List<String> vehicleIds) {
        for (String currentVehicleId : vehicleIds) {
            if (isCurrentVehiclesTemperatureHigh(currentVehicleId)) {
                return true;
            }
        }
        return false;
    }

    private void stopHyperDriveForOneVehicle(String vehicleId) {
        HyperdriveStopRequest stopRequest = HyperdriveStopRequest.builder()
                .cruiseSpeed(DEFAULT_CRUISE_SPEED).build();
        try {
            HyperdriveStopResponse stopResponse = hyperdriveClient.stop(vehicleId, stopRequest);
        } catch (ApiException | JsonProcessingException e) {
            LOGGER.warn("Error trying to stop hperdrive for vehicle id {}!! Ignoring this error!!", vehicleId, e);
        }
    }

    //Step 5 Mission Requirements Document
    private void monitorOneMission(final Fleet fleet) {
        if (fleet == null) {
            return;
        }
        Date currentTime = new Date();
        if (currentTime.after(fleet.getMissionEndTime())) {
            LOGGER.info("Current Time {} is after fleets mission end time {}, marking the mission as complete!!", currentTime, fleet.getMissionEndTime());
            missionService.completeMission(fleet.getId(), fleet.getMissionId());
            return;
        }

        List<String> vehicleIds;
        try {
            vehicleIds = fleetNetClient.getVehiclesInAFleet(fleet.getId());
        } catch (ApiException e) {
            LOGGER.error("Error fetching all vehicles in Fleet {}", fleet, e);
            return;
        }
        if (CollectionUtils.isEmpty(vehicleIds)) {
            return;
        }
        if (!isAnyVehiclesTemperatureHigh(vehicleIds)) {
            LOGGER.info("All vehicles in fleet {} had safe temperatures!", fleet);
            return;
        }
        for (String currentVehicleId : vehicleIds) {
            stopHyperDriveForOneVehicle(currentVehicleId);
        }
        missionService.abortMission(fleet.getId(), fleet.getMissionId());
    }

    private void monitorAllMissions() {
        List<Fleet> fleets = missionService.getAllFleetsOnMissions();
        if (CollectionUtils.isEmpty(fleets)) {
            return;
        }
        for (Fleet currentFleet : fleets) {
            monitorOneMission(currentFleet);
        }
    }

    @Async
    public void doAsync() throws InterruptedException {
        while (true) {
            monitorAllMissions();
            //Sleep for 10 minutes between each check
            //ToDo: This is an assumption!
            Thread.sleep(TimeUnit.MINUTES.toMillis(1));
        }
    }
}
