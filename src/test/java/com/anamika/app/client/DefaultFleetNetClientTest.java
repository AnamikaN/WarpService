package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.anamika.app.config.DefaultHyperdriveClientConfig;
import com.anamika.app.config.JacksonConfig;
import com.anamika.app.service.DefaultServiceURIsFetcher;
import com.anamika.app.service.RoundRobinServiceInstanceProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
@RunWith(SpringRunner.class)
@Import(value = {DefaultFleetNetClient.class, RoundRobinServiceInstanceProvider.class, DefaultServiceURIsFetcher.class, DefaultHyperdriveClientConfig.class, JacksonConfig.class})
public class DefaultFleetNetClientTest {
    @Autowired
    private DefaultFleetNetClient client;
    @Autowired
    private ObjectMapper objectMapper;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private static final String FLEET_ID = "fleet_id";
    private static final String VEHICLE_IDS = "[\"vehicle_id_1\", \"vehicle_id_2\", \"vehicle_id_3\", \"vehicle_id_4\", \"vehicle_id_5\"]";
    private static final List<String> VEHICLE_ID_LIST = ImmutableList.<String>builder()
            .add("vehicle_id_1").add("vehicle_id_2").add("vehicle_id_3").add("vehicle_id_4").add("vehicle_id_5").build();

    @Test
    public void testStatusSuccessful1Call() throws JsonProcessingException {
        stubFor(get(urlPathMatching("/fleetnet.service/" + FLEET_ID))
                .willReturn(aResponse().withStatus(200).withBody(VEHICLE_IDS)));

        try {
            List<String> actual = client.getVehiclesInAFleet(FLEET_ID);
            assertEquals(5, actual.size());
            assertTrue(actual.containsAll(VEHICLE_ID_LIST));
        } catch (ApiException e) {
            fail("Exception not expected!!" + e.getStackTrace());
        }
        verify(exactly(1), getRequestedFor(urlEqualTo("/fleetnet.service/" + FLEET_ID))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }
}