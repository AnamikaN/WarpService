package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.service.ServiceInstanceProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 */
@Service
public class DefaultFleetNetClient implements FleetNetClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ServiceInstanceProvider instanceProvider;
    private final Retryer<Response> retryer;
    private final ObjectMapper objectMapper;

    @Autowired
    public DefaultFleetNetClient(ServiceInstanceProvider instanceProvider, Retryer<Response> retryer, ObjectMapper objectMapper) {
        this.instanceProvider = instanceProvider;
        this.retryer = retryer;
        this.objectMapper = objectMapper;
    }

    /**
     * Common function which processes
     *
     * @param callable Callable called by the Retryer
     * @return List of Vehicle Ids
     * @throws ApiException
     */
    private List<String> process(final FleetNetCallable callable) throws ApiException {
        try {
            try (Response response = retryer.call(callable);
                 InputStream stream = response.body().byteStream()) {
                return objectMapper.readValue(stream, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
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
    public List<String> getVehiclesInAFleet(@NotNull String fleetId) throws ApiException {
        FleetNetCallable callable = FleetNetCallable.builder()
                .instanceProvider(instanceProvider)
                .objectMapper(objectMapper)
                .fleetId(fleetId).build();
        return process(callable);
    }
}
