package com.anamika.app.service;

import com.anamika.app.model.Fleet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class FleetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MongoTemplate mongoTemplate;

    @Autowired
    public FleetService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void scheduleNewMission(Fleet fleet) {
        mongoTemplate.insert(fleet);
    }

    public List<Fleet> getAllFleetsOnMissions() {
        Query query = Query.query(Criteria.where("mission_start_time").ne(null))
                .addCriteria(Criteria.where("mission_end_time").ne(null));
        return mongoTemplate.find(query, Fleet.class);
    }

    public Optional<Fleet> findFleet(final String fleetId) {
        Query query = Query.query(Criteria.where("_id").is(fleetId));
        return Optional.ofNullable(mongoTemplate.findOne(query, Fleet.class));
    }

    public void abortMission(final String fleetId) {
        Query query = Query.query(Criteria.where("_id").is(fleetId));
        Update update = new Update().unset("mission_start_time").unset("mission_end_time");
        mongoTemplate.updateFirst(query, update, Fleet.class);
    }

    public void completeMission(final String fleetId) {
        Query query = Query.query(Criteria.where("_id").is(fleetId));
        Update update = new Update().unset("mission_start_time").unset("mission_end_time");
        mongoTemplate.updateFirst(query, update, Fleet.class);
    }
}
