package com.anamika.app.client;

import com.anamika.app.client.exception.ApiException;
import com.anamika.app.service.ServiceInstanceProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

import static com.anamika.app.utils.Constants.HYPERDRIVE_SERVICE_NAME;

/**
 *
 */
public abstract class HyperdriveCallable implements Callable<Response> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build();

    public abstract ObjectMapper getObjectMapper();

    public abstract ServiceInstanceProvider getInstanceProvider();

    public abstract String getPath();

    public abstract HttpMethod getHttpMethod();

    public abstract String getVehicleId();

    public abstract String getPostData();

    @Override
    public Response call() throws Exception {
        final URI serviceEndpoint = getInstanceProvider().getInstance(HYPERDRIVE_SERVICE_NAME);
        final URI uri = UriComponentsBuilder
                .fromUri(serviceEndpoint)
                .path(getPath())
                .path(getVehicleId())
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
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE), getPostData());
                builder.post(body);
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
