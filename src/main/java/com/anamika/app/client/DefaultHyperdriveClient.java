package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.request.HyperdriveStartRequest;
import com.anamika.app.client.request.HyperdriveStopRequest;
import com.anamika.app.client.response.HyperdriveResponse;
import com.anamika.app.client.response.HyperdriveStartResponse;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.anamika.app.client.response.HyperdriveStopResponse;
import com.anamika.app.service.ServiceInstanceProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutionException;

/**
 *
 */
@Service
@Primary
public class DefaultHyperdriveClient implements HyperdriveClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ServiceInstanceProvider instanceProvider;
    private final Retryer<Response> retryer;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultHyperdriveClient(ServiceInstanceProvider instanceProvider, Retryer<Response> retryer, ObjectMapper objectMapper) {
        this.instanceProvider = instanceProvider;
        this.retryer = retryer;
        this.objectMapper = objectMapper;
    }

    /**
     * Common function which processes Start, Stop & Status calls & returns appropriate response object
     *
     * @param callable Callable called by the Retryer
     * @param clazz    Class of the Response Object
     * @return appropriate response object
     * @throws ApiException
     */
    private <T extends HyperdriveResponse> T process(final HyperdriveCallable callable, Class<T> clazz) throws ApiException {
        try {
            try (Response response = retryer.call(callable);
                 InputStream stream = response.body().byteStream()) {
                return objectMapper.readValue(stream, clazz);
            }
        } catch (ExecutionException | RetryException e) {
            // The retryer returns either ExecutionException or RetryException
            // This wraps the real exception in it.
            // We check the cause & if the cause is of type ApiException, we throw ApiException & discard the wrapper
            if (e.getCause() instanceof ApiException) {
                throw (ApiException) e.getCause();
            }
            // If the cause is not of the type ApiException, we wrap it in RuntimeException.
            // I prefer this, as the exception is unexpected, we cannot handle it, so no point making it checked, also keeps interfaces clean.
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HyperdriveStartResponse start(@NotNull String vehicleId, @NotNull HyperdriveStartRequest request) throws ApiException, JsonProcessingException {
        String postData = objectMapper.writeValueAsString(request);
        HyperdriveCallable callable = HyperdriveStartCallable.builder()
                .objectMapper(objectMapper)
                .instanceProvider(instanceProvider)
                .vehicleId(vehicleId)
                .postData(postData)
                .build();
        HyperdriveStartResponse response = process(callable, HyperdriveStartResponse.class);
        return response;
    }

    @Override
    public HyperdriveStopResponse stop(@NotNull String vehicleId, @NotNull HyperdriveStopRequest request) throws ApiException, JsonProcessingException {
        String postData = objectMapper.writeValueAsString(request);
        HyperdriveCallable callable = HyperdriveStopCallable.builder()
                .objectMapper(objectMapper)
                .instanceProvider(instanceProvider)
                .vehicleId(vehicleId)
                .postData(postData)
                .build();
        HyperdriveStopResponse response = process(callable, HyperdriveStopResponse.class);
        return response;
    }

    @Override
    public HyperdriveStatusResponse status(@NotNull String vehicleId) throws ApiException {
        HyperdriveCallable callable = HyperdriveStatusCallable.builder()
                .objectMapper(objectMapper)
                .instanceProvider(instanceProvider)
                .vehicleId(vehicleId)
                .build();
        HyperdriveStatusResponse response = process(callable, HyperdriveStatusResponse.class);
        return response;
    }
}
