package com.anamika.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * When we start a mission, end a mission or abort a mission, we create a read-only entry (event log).
 * This is meant for source of truth/auditing.
 */
@Document(collection = "mission_event_logs")
@Data
@Builder(toBuilder = true)
public class MissionEventLog {
    @Field(value = "mission_id")
    private final String missionId;

    @Field(value = "fleet_id")
    private final String fleetId;

    private final Integer duration;

    @Field(value = "warp_speed")
    private final Double warpSpeed;

    @Field(value = "event_type")
    private final EventType eventType;

    @Field(value = "created_at")
    private final Date createdAt;

    public enum EventType {
        CREATE,
        END,
        ABORT;

        @JsonCreator
        public static EventType fromString(final String input) {
            Preconditions.checkArgument(!StringUtils.isEmpty(input), "Input value cannot be null or empty!!");
            return valueOf(input.toUpperCase());
        }

        @JsonValue
        public String toString() {
            return this.name().toUpperCase();
        }
    }
}
