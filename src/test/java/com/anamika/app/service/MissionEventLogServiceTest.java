package com.anamika.app.service;

import com.anamika.app.config.MongoConfig;
import com.anamika.app.model.MissionEventLog;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.anamika.app.model.MissionEventLog.EventType.ABORT;
import static com.anamika.app.model.MissionEventLog.EventType.END;
import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(SpringRunner.class)
@Import(value = {MongoConfig.class, MissionEventLogService.class})
public class MissionEventLogServiceTest {

    private static final String FLEET_ID_1 = "fleet_id_1";
    private static final String FLEET_ID_2 = "fleet_id_2";
    private static final String FLEET_ID_3 = "fleet_id_3";
    private static final String FLEET_ID_4 = "fleet_id_4";
    private static final String MISSION_ID_1 = "mission_id_1";
    private static final String MISSION_ID_2 = "mission_id_2";
    private static final String MISSION_ID_3 = "mission_id_3";
    private static final String MISSION_ID_4 = "mission_id_4";
    private static final String MISSION_ID_DOES_NOT_EXIST = "mission_id_does_not_exist";
    private static final Integer DURATION = 2;
    private static final double WARP_SPEED = 0.8;
    private static final DateTime START_TIME = DateTime.now();
    private static final MissionEventLog MISSION_EVENT_LOG_1 = MissionEventLog.builder()
            .missionId(MISSION_ID_1).fleetId(FLEET_ID_1).duration(DURATION).warpSpeed(WARP_SPEED)
            .eventType(MissionEventLog.EventType.CREATE).createdAt(START_TIME.toDate()).build();
    private static final MissionEventLog MISSION_EVENT_LOG_2 = MissionEventLog.builder()
            .missionId(MISSION_ID_2).fleetId(FLEET_ID_2).duration(DURATION).warpSpeed(WARP_SPEED)
            .eventType(MissionEventLog.EventType.CREATE).createdAt(START_TIME.toDate()).build();
    private static final MissionEventLog MISSION_EVENT_LOG_3 = MissionEventLog.builder()
            .missionId(MISSION_ID_3).fleetId(FLEET_ID_3).duration(DURATION).warpSpeed(WARP_SPEED)
            .eventType(MissionEventLog.EventType.CREATE).createdAt(START_TIME.toDate()).build();
    private static final MissionEventLog MISSION_EVENT_LOG_4 = MissionEventLog.builder()
            .missionId(MISSION_ID_4).fleetId(FLEET_ID_4).duration(DURATION).warpSpeed(WARP_SPEED)
            .eventType(MissionEventLog.EventType.CREATE).createdAt(START_TIME.toDate()).build();
    private static final MissionEventLog MISSION_EVENT_LOG_3_COMPLETED = MissionEventLog.builder()
            .missionId(MISSION_ID_3).fleetId(FLEET_ID_3).duration(DURATION).warpSpeed(WARP_SPEED)
            .eventType(MissionEventLog.EventType.END).createdAt(START_TIME.toDate()).build();
    private static final MissionEventLog MISSION_EVENT_LOG_4_ABORTED = MissionEventLog.builder()
            .missionId(MISSION_ID_4).fleetId(FLEET_ID_4).duration(DURATION).warpSpeed(WARP_SPEED)
            .eventType(MissionEventLog.EventType.ABORT).createdAt(START_TIME.toDate()).build();

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MissionEventLogService service;

    @Before
    public void setup() throws Exception {
        mongoTemplate.dropCollection(MissionEventLog.class);
    }

    @Test
    public void testMissionEventLogService() {
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_4));

        //Save Mission Event Log to Db
        service.logMissionCreated(MISSION_EVENT_LOG_1);
        assertEquals(Optional.of(MISSION_EVENT_LOG_1), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_4));

        //Save Mission Event Log to Db
        service.logMissionCreated(MISSION_EVENT_LOG_2);
        assertEquals(Optional.of(MISSION_EVENT_LOG_1), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.of(MISSION_EVENT_LOG_2), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_4));

        //Save Mission Event Log to Db
        service.logMissionCreated(MISSION_EVENT_LOG_3);
        assertEquals(Optional.of(MISSION_EVENT_LOG_1), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.of(MISSION_EVENT_LOG_2), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.of(MISSION_EVENT_LOG_3), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.empty(), service.findCreateMissionEventLog(MISSION_ID_4));

        //Save Mission Event Log to Db
        service.logMissionCreated(MISSION_EVENT_LOG_4);
        assertEquals(Optional.of(MISSION_EVENT_LOG_1), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.of(MISSION_EVENT_LOG_2), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.of(MISSION_EVENT_LOG_3), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.of(MISSION_EVENT_LOG_4), service.findCreateMissionEventLog(MISSION_ID_4));

        service.logMissionCompleted(MISSION_ID_3);
        service.logMissionCompleted(MISSION_ID_DOES_NOT_EXIST);
        assertEquals(Optional.of(MISSION_EVENT_LOG_1), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.of(MISSION_EVENT_LOG_2), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.of(MISSION_EVENT_LOG_3), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.of(MISSION_EVENT_LOG_4), service.findCreateMissionEventLog(MISSION_ID_4));
        assertEquals(Optional.of(MISSION_EVENT_LOG_3_COMPLETED), service.findMissionEventLogByEventType(MISSION_ID_3, END));

        service.logMissionAborted(MISSION_ID_4);
        service.logMissionAborted(MISSION_ID_DOES_NOT_EXIST);
        assertEquals(Optional.of(MISSION_EVENT_LOG_1), service.findCreateMissionEventLog(MISSION_ID_1));
        assertEquals(Optional.of(MISSION_EVENT_LOG_2), service.findCreateMissionEventLog(MISSION_ID_2));
        assertEquals(Optional.of(MISSION_EVENT_LOG_3), service.findCreateMissionEventLog(MISSION_ID_3));
        assertEquals(Optional.of(MISSION_EVENT_LOG_4), service.findCreateMissionEventLog(MISSION_ID_4));
        assertEquals(Optional.of(MISSION_EVENT_LOG_3_COMPLETED), service.findMissionEventLogByEventType(MISSION_ID_3, END));
        assertEquals(Optional.of(MISSION_EVENT_LOG_4_ABORTED), service.findMissionEventLogByEventType(MISSION_ID_4, ABORT));
    }
}
