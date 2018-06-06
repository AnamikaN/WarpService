package com.anamika.app.client.response;

import com.anamika.app.config.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


/**
 *
 */
@RunWith(SpringRunner.class)
@Import(value = {JacksonConfig.class})
public class HyperdriveStatusResponseTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testConstructor1() throws IOException {
        HyperdriveStatusResponse obj1 = HyperdriveStatusResponse.builder()
                .cruiseSpeed(0.0089)
                .temperature(128).build();
        assertEquals(obj1.getCruiseSpeed(), new Double(0.0089));
        assertEquals(obj1.getTemperature(), new Integer(128));
        assertNull(obj1.getWarpSpeed());
        assertNull(obj1.getDuration());
        String actualString = objectMapper.writeValueAsString(obj1);
        String expectedString = "{\"cruise_speed\":0.0089,\"temperature\":128}";
        assertEquals(actualString, expectedString);
        HyperdriveStatusResponse obj2 = objectMapper.readValue(actualString, HyperdriveStatusResponse.class);
        assertEquals(obj1, obj2);
    }

    @Test
    public void testConstructor2() throws IOException {
        HyperdriveStatusResponse obj1 = HyperdriveStatusResponse.builder()
                .warpSpeed(0.8)
                .duration(120)
                .temperature(128).build();
        assertEquals(obj1.getWarpSpeed(), new Double(0.8));
        assertEquals(obj1.getDuration(), new Integer(120));
        assertEquals(obj1.getTemperature(), new Integer(128));
        assertNull(obj1.getCruiseSpeed());
        String actualString = objectMapper.writeValueAsString(obj1);
        String expectedString = "{\"warp_speed\":0.8,\"duration\":120,\"temperature\":128}";
        assertEquals(actualString, expectedString);
        HyperdriveStatusResponse obj2 = objectMapper.readValue(actualString, HyperdriveStatusResponse.class);
        assertEquals(obj1, obj2);
    }
}