package com.anamika.app.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 *
 */
@Document(collection = "fleets")
@Data
@Builder(toBuilder = true)
public class Fleet {
    @Id
    private final String id; //Fleet Id

    @Field(value = "mission_id")
    private final String missionId;

    @Field(value = "mission_start_time")
    private final Date missionStartTime;

    @Field(value = "mission_end_time")
    private final Date missionEndTime;

    private final Integer duration;

    @Field(value = "warp_speed")
    private final Double warpSpeed;
}
