package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.request.HyperdriveStartRequest;
import com.anamika.app.client.request.HyperdriveStopRequest;
import com.anamika.app.client.response.HyperdriveStartResponse;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.anamika.app.client.response.HyperdriveStopResponse;
import com.anamika.app.config.DefaultHyperdriveClientConfig;
import com.anamika.app.config.JacksonConfig;
import com.anamika.app.service.DefaultServiceURIsFetcher;
import com.anamika.app.service.RoundRobinServiceInstanceProvider;
import com.anamika.app.service.ServiceInstanceProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {JacksonConfig.class})
public class DefaultHyperdriveClientIntegTest {
    private final ServiceInstanceProvider instanceProvider = new RoundRobinServiceInstanceProvider(
            new DefaultServiceURIsFetcher(), 5, TimeUnit.MINUTES
    );
    @Autowired
    private ObjectMapper objectMapper;

    //@Test
    public void testStart() throws JsonProcessingException, ApiException {
        final HyperdriveClient client = new DefaultHyperdriveClient(instanceProvider, DefaultHyperdriveClientConfig.DEFAULT_RETRYER, objectMapper);
        HyperdriveStartRequest request = HyperdriveStartRequest.builder()
                .duration(120).warpSpeed(0.8)
                .build();
        HyperdriveStartResponse response = client.start("vehicle_id", request);
    }

    //@Test
    public void testStop() throws JsonProcessingException, ApiException {
        final HyperdriveClient client = new DefaultHyperdriveClient(instanceProvider, DefaultHyperdriveClientConfig.DEFAULT_RETRYER, objectMapper);
        HyperdriveStopRequest request = HyperdriveStopRequest.builder()
                .cruiseSpeed(0.001)
                .build();
        HyperdriveStopResponse response = client.stop("vehicle_id", request);
    }

    //@Test
    public void testStatus() throws JsonProcessingException, ApiException {
        final HyperdriveClient client = new DefaultHyperdriveClient(instanceProvider, DefaultHyperdriveClientConfig.DEFAULT_RETRYER, objectMapper);
        HyperdriveStatusResponse response = client.status("vehicle_id");
    }
}