package com.anamika.app.service;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

/**
 * {@link ServiceURIsFetcher} class provides a list of URIs for any internal services that support Service Discovery.
 * For internal services that register with service discovery tools like Hashicorp Consul or Eureka etc., this service
 * will fetch the list of URIs.
 */
public interface ServiceURIsFetcher {
    Set<URI> getInstances(@NotNull final String serviceName);
}
