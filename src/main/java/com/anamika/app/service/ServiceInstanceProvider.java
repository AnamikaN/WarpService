package com.anamika.app.service;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * {@link ServiceInstanceProvider} is responsible to return one URI for a given Service
 */
public interface ServiceInstanceProvider {
    URI getInstance(@NotNull final String serviceName);
}
