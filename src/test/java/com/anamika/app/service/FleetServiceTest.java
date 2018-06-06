package com.anamika.app.service;

import com.anamika.app.config.MongoConfig;
import com.anamika.app.model.Fleet;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(SpringRunner.class)
@Import(value = {MongoConfig.class, FleetService.class})
public class FleetServiceTest {
    private static final String FLEET_ID_1 = "fleet_id_1";
    private static final String FLEET_ID_2 = "fleet_id_2";
    private static final String FLEET_ID_3 = "fleet_id_3";
    private static final Integer DURATION = 2;
    private static final double WARP_SPEED = 0.8;
    private static final String MISSION_ID_1 = UUID.randomUUID().toString();
    private static final String MISSION_ID_2 = UUID.randomUUID().toString();
    private static final DateTime START_TIME = DateTime.now();
    private static final DateTime END_TIME = START_TIME.plusHours(DURATION);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FleetService service;

    @Before
    public void setup() throws Exception {
        mongoTemplate.dropCollection(Fleet.class);
    }

    @Test
    public void testFleetService() {
        //Generate Fleet Object
        Fleet fleet1 = Fleet.builder()
                .id(FLEET_ID_1).missionId(MISSION_ID_1).duration(DURATION).warpSpeed(WARP_SPEED)
                .missionStartTime(START_TIME.toDate()).missionEndTime(END_TIME.toDate()).build();

        assertEquals(Optional.empty(), service.findFleet(FLEET_ID_1));
        assertEquals(Optional.empty(), service.findFleet(FLEET_ID_2));
        assertTrue(CollectionUtils.isEmpty(service.getAllFleetsOnMissions()));
        service.scheduleNewMission(fleet1);
        assertEquals(Optional.of(fleet1), service.findFleet(FLEET_ID_1));
        assertEquals(Optional.empty(), service.findFleet(FLEET_ID_2));
        assertEquals(1, service.getAllFleetsOnMissions().size());
        assertTrue(service.getAllFleetsOnMissions().contains(fleet1));

        Fleet fleet2 = Fleet.builder()
                .id(FLEET_ID_2).missionId(MISSION_ID_2).duration(DURATION).warpSpeed(WARP_SPEED)
                .missionStartTime(START_TIME.toDate()).missionEndTime(END_TIME.toDate()).build();

        service.scheduleNewMission(fleet2);
        assertEquals(Optional.of(fleet1), service.findFleet(FLEET_ID_1));
        assertEquals(Optional.of(fleet2), service.findFleet(FLEET_ID_2));
        assertEquals(2, service.getAllFleetsOnMissions().size());
        assertTrue(service.getAllFleetsOnMissions().contains(fleet1));
        assertTrue(service.getAllFleetsOnMissions().contains(fleet2));

        Fleet fleet1Aborted = fleet1.toBuilder().missionStartTime(null).missionEndTime(null).build();
        service.abortMission(FLEET_ID_1);
        assertEquals(Optional.of(fleet1Aborted), service.findFleet(FLEET_ID_1));
        assertEquals(Optional.of(fleet2), service.findFleet(FLEET_ID_2));
        assertEquals(1, service.getAllFleetsOnMissions().size());
        assertTrue(service.getAllFleetsOnMissions().contains(fleet2));

        Fleet fleet2Completed = fleet2.toBuilder().missionStartTime(null).missionEndTime(null).build();
        service.completeMission(FLEET_ID_2);
        assertEquals(Optional.of(fleet1Aborted), service.findFleet(FLEET_ID_1));
        assertEquals(Optional.of(fleet2Completed), service.findFleet(FLEET_ID_2));
        assertTrue(CollectionUtils.isEmpty(service.getAllFleetsOnMissions()));
    }
}
