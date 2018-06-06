package com.anamika.app.config;

import com.anamika.app.client.exception.ApiException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;
import okhttp3.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Configuration
public class DefaultHyperdriveClientConfig {
    public static final Retryer<Response> DEFAULT_RETRYER = RetryerBuilder.<Response>newBuilder()
            .retryIfExceptionOfType(ConnectException.class)
            .retryIfException(RetryPredicate.INSTANCE)
            .withWaitStrategy(WaitStrategies.exponentialWait(100, 1, TimeUnit.MINUTES))
            .withStopStrategy(StopStrategies.stopAfterAttempt(5))
            .build();

    enum RetryPredicate implements Predicate<Throwable> {
        INSTANCE {
            @Override
            public boolean apply(@Nullable Throwable input) {
                if (input instanceof ApiException) {
                    ApiException apiException = (ApiException) input;
                    if (apiException.getResponseStatus().is5xxServerError()) {
                        return true;
                    }
                    if (apiException.getResponseStatus() == HttpStatus.TOO_MANY_REQUESTS) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Bean
    public Retryer<Response> getRetryer() {
        return DEFAULT_RETRYER;
    }

    @Bean("instancesCacheDuration")
    public int getInstancesCacheDuration() {
        return 5;
    }

    @Bean("instancesCacheDurationTimeUnit")
    public TimeUnit getInstancesCacheDurationTimeUnit() {
        return TimeUnit.MINUTES;
    }
}
