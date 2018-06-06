package com.anamika.app.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static com.anamika.app.utils.Constants.FLEET_NET_SERVICE_NAME;
import static com.anamika.app.utils.Constants.HYPERDRIVE_SERVICE_NAME;

/**
 * Ideally {@link DefaultServiceURIsFetcher} would use Service Discovery Tool's (Consul/Eureka) client library or sdk
 * and get the unique list of URI for any supported service.
 * For this coding exercise, for both the Hyperdrive service & FleetNet Service it will return only one url http://localhost:8080.
 */
@Service
@Primary
public class DefaultServiceURIsFetcher implements ServiceURIsFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final URI DEFAULT_URI = URI.create("http://localhost:8080");
    private static final Set<URI> DEFAULT_URIS = ImmutableSet.<URI>builder()
            .add(DEFAULT_URI).build();
    private static final Set<URI> EMPTY_URIS = Collections.emptySet();

    @Override
    public Set<URI> getInstances(@NotNull String serviceName) {
        Preconditions.checkArgument(!StringUtils.isEmpty(serviceName), "Input Service Name cannot be null or empty!");
        if ((HYPERDRIVE_SERVICE_NAME.equals(serviceName)) || (FLEET_NET_SERVICE_NAME.equals(serviceName))) {
            return DEFAULT_URIS;
        }
        return EMPTY_URIS;
    }
}
