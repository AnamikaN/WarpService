package com.anamika.app.client;


import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.request.HyperdriveStartRequest;
import com.anamika.app.client.request.HyperdriveStopRequest;
import com.anamika.app.client.response.HyperdriveStartResponse;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.anamika.app.client.response.HyperdriveStopResponse;
import com.anamika.app.config.DefaultHyperdriveClientConfig;
import com.anamika.app.config.JacksonConfig;
import com.anamika.app.error.MessageDTO;
import com.anamika.app.service.DefaultServiceURIsFetcher;
import com.anamika.app.service.RoundRobinServiceInstanceProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 *
 */
@RunWith(SpringRunner.class)
@Import(value = {DefaultHyperdriveClient.class, RoundRobinServiceInstanceProvider.class, DefaultServiceURIsFetcher.class, DefaultHyperdriveClientConfig.class, JacksonConfig.class})
public class DefaultHyperdriveClientTest {
    //Vehicle Ids
    private static final String VEHICLE_200_1 = "vehicle_200_1";
    private static final String VEHICLE_200_2 = "vehicle_200_2";
    private static final String VEHICLE_400_1 = "vehicle_400_1";
    private static final String VEHICLE_400_2 = "vehicle_400_2";
    private static final String VEHICLE_404 = "vehicle_404";
    private static final String VEHICLE_429 = "vehicle_429";
    private static final String VEHICLE_500 = "vehicle_500";

    //Requests
    private static final HyperdriveStartRequest startRequest = HyperdriveStartRequest.builder()
            .warpSpeed(0.8).duration(120).build();
    private static final HyperdriveStopRequest stopRequest = HyperdriveStopRequest.builder()
            .cruiseSpeed(0.00089).build();

    //Success Response
    private static final HyperdriveStartResponse startResponse = HyperdriveStartResponse.builder()
            .warpSpeed(0.8).duration(120).temperature(278).build();
    private static final HyperdriveStopResponse stopResponse = HyperdriveStopResponse.builder()
            .cruiseSpeed(0.00089).temperature(280).build();
    private static final HyperdriveStatusResponse statusResponse1 = HyperdriveStatusResponse.builder()
            .warpSpeed(0.8).duration(120).temperature(278).build();
    private static final HyperdriveStatusResponse statusResponse2 = HyperdriveStatusResponse.builder()
            .cruiseSpeed(0.00089).temperature(278).build();

    //Error Response
    //Error Response - 400
    private static final MessageDTO BAD_REQUEST_START_1 = MessageDTO.builder()
            .message("unable to start, hyperdrive temperature is above 1000 K").build();
    private static final MessageDTO BAD_REQUEST_START_2 = MessageDTO.builder()
            .message("vehicle is already at warp speed").build();
    private static final MessageDTO BAD_REQUEST_STOP_1 = MessageDTO.builder()
            .message("Invalid cruise speed, must be between (0.0 - 0.001)").build();

    //Error Response - 404
    private static final MessageDTO RESOURCE_NOT_FOUND = MessageDTO.builder()
            .message("Resource Not Found").build();

    //Error Response - 429
    private static final MessageDTO TOO_MANY_REQUESTS = MessageDTO.builder()
            .message("Too Many Requests").build();
    private static final MessageDTO INTERNAL_SERVER_ERROR = MessageDTO.builder()
            .message("Internal Server Error").build();

    @Autowired
    private DefaultHyperdriveClient client;
    @Autowired
    private ObjectMapper objectMapper;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    public String startRequestString;
    public String stopRequestString;

    @Before
    public void setUp() throws JsonProcessingException {
        startRequestString = objectMapper.writeValueAsString(startRequest);
        stopRequestString = objectMapper.writeValueAsString(stopRequest);
    }

    @Test
    public void testStartSuccessfulCall() throws JsonProcessingException {
        final String successStartResponseString = objectMapper.writeValueAsString(startResponse);
        stubFor(post(urlPathMatching("/hyperdrive.service/start/" + VEHICLE_200_1))
                .willReturn(aResponse().withStatus(200).withBody(successStartResponseString)));

        try {
            HyperdriveStartResponse actual = client.start(VEHICLE_200_1, startRequest);
            assertEquals(startResponse, actual);
        } catch (ApiException | JsonProcessingException e) {
            fail("Exception not expected!!" + e.getStackTrace());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/start/" + VEHICLE_200_1))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(startRequestString)));
    }

    @Test
    public void testStopSuccessfulCall() throws JsonProcessingException {
        final String successStopResponseString = objectMapper.writeValueAsString(stopResponse);
        stubFor(post(urlPathMatching("/hyperdrive.service/stop/" + VEHICLE_200_2))
                .willReturn(aResponse().withStatus(200).withBody(successStopResponseString)));
        try {
            HyperdriveStopResponse actual = client.stop(VEHICLE_200_2, stopRequest);
            assertEquals(stopResponse, actual);
        } catch (ApiException | JsonProcessingException e) {
            fail("Exception not expected!!" + e.getStackTrace());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/stop/" + VEHICLE_200_2))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(stopRequestString)));
    }

    @Test
    public void testStatusSuccessful1Call() throws JsonProcessingException {
        final String statusResponse1String = objectMapper.writeValueAsString(statusResponse1);
        stubFor(get(urlPathMatching("/hyperdrive.service/status/" + VEHICLE_200_1))
                .willReturn(aResponse().withStatus(200).withBody(statusResponse1String)));

        try {
            HyperdriveStatusResponse actual = client.status(VEHICLE_200_1);
            assertEquals(statusResponse1, actual);
        } catch (ApiException e) {
            fail("Exception not expected!!" + e.getStackTrace());
        }
        verify(exactly(1), getRequestedFor(urlEqualTo("/hyperdrive.service/status/" + VEHICLE_200_1))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    public void testStatusSuccessful2Call() throws JsonProcessingException {
        final String statusResponse2String = objectMapper.writeValueAsString(statusResponse2);
        stubFor(get(urlPathMatching("/hyperdrive.service/status/" + VEHICLE_200_2))
                .willReturn(aResponse().withStatus(200).withBody(statusResponse2String)));

        try {
            HyperdriveStatusResponse actual = client.status(VEHICLE_200_2);
            assertEquals(statusResponse2, actual);
        } catch (ApiException e) {
            fail("Exception not expected!!" + e.getStackTrace());
        }
        verify(exactly(1), getRequestedFor(urlEqualTo("/hyperdrive.service/status/" + VEHICLE_200_2))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    public void testStartBadRequest1() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(BAD_REQUEST_START_1);
        stubFor(post(urlPathMatching("/hyperdrive.service/start/" + VEHICLE_400_1))
                .willReturn(aResponse().withStatus(400).withBody(responseString)));

        try {
            HyperdriveStartResponse actual = client.start(VEHICLE_400_1, startRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.BAD_REQUEST);
            assertEquals(e.getErrorMessage(), BAD_REQUEST_START_1.getMessage());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/start/" + VEHICLE_400_1))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(startRequestString)));
    }

    @Test
    public void testStartBadRequest2() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(BAD_REQUEST_START_2);
        stubFor(post(urlPathMatching("/hyperdrive.service/start/" + VEHICLE_400_2))
                .willReturn(aResponse().withStatus(400).withBody(responseString)));

        try {
            HyperdriveStartResponse actual = client.start(VEHICLE_400_2, startRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.BAD_REQUEST);
            assertEquals(e.getErrorMessage(), BAD_REQUEST_START_2.getMessage());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/start/" + VEHICLE_400_2))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(startRequestString)));
    }

    @Test
    public void testStopBadRequest1() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(BAD_REQUEST_STOP_1);
        stubFor(post(urlPathMatching("/hyperdrive.service/stop/" + VEHICLE_400_1))
                .willReturn(aResponse().withStatus(400).withBody(responseString)));
        try {
            HyperdriveStopResponse actual = client.stop(VEHICLE_400_1, stopRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.BAD_REQUEST);
            assertEquals(e.getErrorMessage(), BAD_REQUEST_STOP_1.getMessage());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/stop/" + VEHICLE_400_1))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(stopRequestString)));
    }

    @Test
    public void testStartNotFound() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(RESOURCE_NOT_FOUND);
        stubFor(post(urlPathMatching("/hyperdrive.service/start/" + VEHICLE_404))
                .willReturn(aResponse().withStatus(404).withBody(responseString)));

        try {
            HyperdriveStartResponse actual = client.start(VEHICLE_404, startRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getErrorMessage(), RESOURCE_NOT_FOUND.getMessage());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/start/" + VEHICLE_404))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(startRequestString)));
    }

    @Test
    public void testStopNotFound() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(RESOURCE_NOT_FOUND);
        stubFor(post(urlPathMatching("/hyperdrive.service/stop/" + VEHICLE_404))
                .willReturn(aResponse().withStatus(404).withBody(responseString)));
        try {
            HyperdriveStopResponse actual = client.stop(VEHICLE_404, stopRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getErrorMessage(), RESOURCE_NOT_FOUND.getMessage());
        }
        verify(exactly(1), postRequestedFor(urlEqualTo("/hyperdrive.service/stop/" + VEHICLE_404))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(stopRequestString)));
    }

    @Test
    public void testStatusNotFound() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(RESOURCE_NOT_FOUND);
        stubFor(get(urlPathMatching("/hyperdrive.service/status/" + VEHICLE_404))
                .willReturn(aResponse().withStatus(404).withBody(responseString)));

        try {
            HyperdriveStatusResponse actual = client.status(VEHICLE_404);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.NOT_FOUND);
            assertEquals(e.getErrorMessage(), RESOURCE_NOT_FOUND.getMessage());
        }
        verify(exactly(1), getRequestedFor(urlEqualTo("/hyperdrive.service/status/" + VEHICLE_404))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    public void testStartTooManyRequests() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(TOO_MANY_REQUESTS);
        stubFor(post(urlPathMatching("/hyperdrive.service/start/" + VEHICLE_429))
                .willReturn(aResponse().withStatus(429).withBody(responseString)));

        try {
            HyperdriveStartResponse actual = client.start(VEHICLE_429, startRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.TOO_MANY_REQUESTS);
            assertEquals(e.getErrorMessage(), TOO_MANY_REQUESTS.getMessage());
        }
        verify(exactly(5), postRequestedFor(urlEqualTo("/hyperdrive.service/start/" + VEHICLE_429))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(startRequestString)));
    }

    @Test
    public void testStopTooManyRequests() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(TOO_MANY_REQUESTS);
        stubFor(post(urlPathMatching("/hyperdrive.service/stop/" + VEHICLE_429))
                .willReturn(aResponse().withStatus(429).withBody(responseString)));
        try {
            HyperdriveStopResponse actual = client.stop(VEHICLE_429, stopRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.TOO_MANY_REQUESTS);
            assertEquals(e.getErrorMessage(), TOO_MANY_REQUESTS.getMessage());
        }
        verify(exactly(5), postRequestedFor(urlEqualTo("/hyperdrive.service/stop/" + VEHICLE_429))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(stopRequestString)));
    }

    @Test
    public void testStatusTooManyRequests() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(TOO_MANY_REQUESTS);
        stubFor(get(urlPathMatching("/hyperdrive.service/status/" + VEHICLE_429))
                .willReturn(aResponse().withStatus(429).withBody(responseString)));

        try {
            HyperdriveStatusResponse actual = client.status(VEHICLE_429);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.TOO_MANY_REQUESTS);
            assertEquals(e.getErrorMessage(), TOO_MANY_REQUESTS.getMessage());
        }
        verify(exactly(5), getRequestedFor(urlEqualTo("/hyperdrive.service/status/" + VEHICLE_429))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    public void testStartInternalError() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(INTERNAL_SERVER_ERROR);
        stubFor(post(urlPathMatching("/hyperdrive.service/start/" + VEHICLE_500))
                .willReturn(aResponse().withStatus(500).withBody(responseString)));

        try {
            HyperdriveStartResponse actual = client.start(VEHICLE_500, startRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
            assertEquals(e.getErrorMessage(), INTERNAL_SERVER_ERROR.getMessage());
        }
        verify(exactly(5), postRequestedFor(urlEqualTo("/hyperdrive.service/start/" + VEHICLE_500))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(startRequestString)));
    }

    @Test
    public void testStopInternalError() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(INTERNAL_SERVER_ERROR);
        stubFor(post(urlPathMatching("/hyperdrive.service/stop/" + VEHICLE_500))
                .willReturn(aResponse().withStatus(500).withBody(responseString)));
        try {
            HyperdriveStopResponse actual = client.stop(VEHICLE_500, stopRequest);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
            assertEquals(e.getErrorMessage(), INTERNAL_SERVER_ERROR.getMessage());
        }
        verify(exactly(5), postRequestedFor(urlEqualTo("/hyperdrive.service/stop/" + VEHICLE_500))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)).withRequestBody(equalTo(stopRequestString)));
    }

    @Test
    public void testStatusInternalError() throws JsonProcessingException {
        final String responseString = objectMapper.writeValueAsString(INTERNAL_SERVER_ERROR);
        stubFor(get(urlPathMatching("/hyperdrive.service/status/" + VEHICLE_500))
                .willReturn(aResponse().withStatus(500).withBody(responseString)));

        try {
            HyperdriveStatusResponse actual = client.status(VEHICLE_500);
            fail("ApiException excpected!!");
        } catch (ApiException e) {
            assertEquals(e.getResponseStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
            assertEquals(e.getErrorMessage(), INTERNAL_SERVER_ERROR.getMessage());
        }
        verify(exactly(5), getRequestedFor(urlEqualTo("/hyperdrive.service/status/" + VEHICLE_500))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }
}