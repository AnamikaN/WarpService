package com.anamika.app.service;

import com.anamika.app.model.MissionEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.anamika.app.model.MissionEventLog.EventType.CREATE;

/**
 *
 */
@Service
public class MissionEventLogService {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MissionEventLogService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void logMissionCreated(@NotNull final MissionEventLog missionEventLog) {
        mongoTemplate.insert(missionEventLog);

    }

    public Optional<MissionEventLog> findCreateMissionEventLog(final String missionId) {
        return findMissionEventLogByEventType(missionId, CREATE);
    }

    public Optional<MissionEventLog> findMissionEventLogByEventType(final String missionId, final MissionEventLog.EventType eventType) {
        Query query = Query.query(Criteria.where("mission_id").is(missionId).and("event_type").is(eventType));
        return Optional.ofNullable(mongoTemplate.findOne(query, MissionEventLog.class));
    }

    public void logMissionCompleted(final String missionId) {
        Optional<MissionEventLog> createdMissionEventLogOptional = findCreateMissionEventLog(missionId);
        if (!createdMissionEventLogOptional.isPresent()) {
            return;
        }
        MissionEventLog missionEventLog = createdMissionEventLogOptional.get();
        MissionEventLog missionEventLogNew = missionEventLog.toBuilder().eventType(MissionEventLog.EventType.END).build();
        mongoTemplate.insert(missionEventLogNew);
    }

    public void logMissionAborted(final String missionId) {
        Optional<MissionEventLog> createdMissionEventLogOptional = findCreateMissionEventLog(missionId);
        if (!createdMissionEventLogOptional.isPresent()) {
            return;
        }
        MissionEventLog missionEventLog = createdMissionEventLogOptional.get();
        MissionEventLog missionEventLogNew = missionEventLog.toBuilder().eventType(MissionEventLog.EventType.ABORT).build();
        mongoTemplate.insert(missionEventLogNew);
    }
}
