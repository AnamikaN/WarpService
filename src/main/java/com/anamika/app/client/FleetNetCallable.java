package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.service.ServiceInstanceProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.anamika.app.utils.Constants.FLEET_NET_SERVICE_NAME;

/**
 *
 */
@Data
@Builder
public class FleetNetCallable implements Callable<Response> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build();

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String PATH = "fleetnet.service/";

    private final ObjectMapper objectMapper;
    private final ServiceInstanceProvider instanceProvider;
    private final String fleetId;

    public String getPath() {
        return PATH;
    }

    public HttpMethod getHttpMethod() {
        return HTTP_METHOD;
    }

    @Override
    public Response call() throws Exception {
        final URI serviceEndpoint = getInstanceProvider().getInstance(FLEET_NET_SERVICE_NAME);
        final URI uri = UriComponentsBuilder
                .fromUri(serviceEndpoint)
                .path(getPath())
                .path(getFleetId())
                .build().toUri();
        LOGGER.debug("uri = {}", uri);

        Request.Builder builder = new Request.Builder()
                .url(uri.toURL())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        switch (getHttpMethod()) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(null);
                break;
            default:
                break;
        }
        Response response = HTTP_CLIENT.newCall(builder.build()).execute();
        HttpStatus status = HttpStatus.valueOf(response.code());
        LOGGER.info("Request = {}, Response Code = {}", uri, status);
        if (status.is2xxSuccessful()) {
            return response;
        }
        throw ApiException.getApiErrorMessage(getObjectMapper(), status, response.body().byteStream());
    }

}
