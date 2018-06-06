package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.client.request.HyperdriveStartRequest;
import com.anamika.app.client.request.HyperdriveStopRequest;
import com.anamika.app.client.response.HyperdriveStartResponse;
import com.anamika.app.client.response.HyperdriveStatusResponse;
import com.anamika.app.client.response.HyperdriveStopResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.constraints.NotNull;

/**
 *
 */
public interface HyperdriveClient {
    HyperdriveStartResponse start(@NotNull String vehicleId, @NotNull HyperdriveStartRequest request) throws ApiException, JsonProcessingException;

    HyperdriveStopResponse stop(@NotNull String vehicleId, @NotNull HyperdriveStopRequest request) throws ApiException, JsonProcessingException;

    HyperdriveStatusResponse status(@NotNull String vehicleId) throws ApiException;
}
