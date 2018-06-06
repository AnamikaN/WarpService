package com.anamika.app.service;

import com.anamika.app.model.Fleet;
import com.anamika.app.model.MissionEventLog;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Service
public class MissionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FleetService fleetService;
    private final MissionEventLogService missionEventLogService;

    @Autowired
    public MissionService(FleetService fleetService, MissionEventLogService missionEventLogService) {
        this.fleetService = fleetService;
        this.missionEventLogService = missionEventLogService;
    }

    public void scheduleNewMission(final String fleetId, final Integer duration, final Double warpSpeed) {
        //Auto Generate mission id
        String missionId = UUID.randomUUID().toString();
        DateTime startTime = DateTime.now();
        DateTime endTime = startTime.plusHours(duration);
        //Generate Fleet Object
        Fleet fleet = Fleet.builder()
                .id(fleetId).missionId(missionId).duration(duration).warpSpeed(warpSpeed)
                .missionStartTime(startTime.toDate()).missionEndTime(endTime.toDate()).build();
        //Save Fleet info to Db
        fleetService.scheduleNewMission(fleet);

        //Generate Mission Event Log
        MissionEventLog missionEventLog = MissionEventLog.builder()
                .missionId(missionId).fleetId(fleetId).duration(duration).warpSpeed(warpSpeed)
                .eventType(MissionEventLog.EventType.CREATE).createdAt(startTime.toDate()).build();
        //Save Mission Event Log to Db
        missionEventLogService.logMissionCreated(missionEventLog);
    }

    public List<Fleet> getAllFleetsOnMissions() {
        return fleetService.getAllFleetsOnMissions();
    }

    public void abortMission(final String fleetId, final String missionId) {
        fleetService.abortMission(fleetId);
        missionEventLogService.logMissionAborted(missionId);
    }

    public void completeMission(final String fleetId, final String missionId) {
        fleetService.completeMission(fleetId);
        missionEventLogService.logMissionCompleted(missionId);
    }
}
