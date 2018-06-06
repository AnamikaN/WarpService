package com.anamika.app.service;

import com.anamika.app.utils.CircularSet;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * {@link RoundRobinServiceInstanceProvider} implements {@link ServiceInstanceProvider}
 * It periodically fetches a list of all URIs for a service and return one of the URIs in Round Robin fashion.
 * It uses {@link com.google.common.cache.LoadingCache} to refresh links periodically.
 * It uses {@link ServiceURIsFetcher} to refresh the unique list of URIs.
 * It uses {@link CircularSet} for Round Robin.
 * It is thread safe.
 */
@Service
@Primary
public class RoundRobinServiceInstanceProvider implements ServiceInstanceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Object LOCK = new Object();

    private final ServiceURIsFetcher serviceURIsFetcher;
    private final LoadingCache<String, CircularSet<URI>> cache;
    private final int instancesCacheDuration;
    private final TimeUnit instancesCacheDurationTimeUnit;

    @Autowired
    public RoundRobinServiceInstanceProvider(ServiceURIsFetcher serviceURIsFetcher,
                                             @Qualifier("instancesCacheDuration") int instancesCacheDuration,
                                             @Qualifier("instancesCacheDurationTimeUnit") TimeUnit instancesCacheDurationTimeUnit) {
        this.serviceURIsFetcher = serviceURIsFetcher;
        this.instancesCacheDuration = instancesCacheDuration;
        this.instancesCacheDurationTimeUnit = instancesCacheDurationTimeUnit;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(instancesCacheDuration, instancesCacheDurationTimeUnit)
                .build(new ServiceInstancesCacheLoader(serviceURIsFetcher));
    }

    @Override
    public URI getInstance(@NotNull String serviceName) {
        Preconditions.checkArgument(!StringUtils.isEmpty(serviceName), "Input service name cannot be null or empty!!");
        CircularSet<URI> circularSet = null;
        try {
            circularSet = this.cache.get(serviceName);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to get instances for service: " + serviceName, e);
        }
        //Thread Safety
        synchronized (LOCK) {
            if (!circularSet.hasNext()) {
                throw new RuntimeException("No instances available for service: " + serviceName);
            }
            return circularSet.next();
        }
    }

    private static final class ServiceInstancesCacheLoader extends CacheLoader<String, CircularSet<URI>> {
        private final ServiceURIsFetcher serviceURIsFetcher;

        private ServiceInstancesCacheLoader(ServiceURIsFetcher serviceURIsFetcher) {
            this.serviceURIsFetcher = serviceURIsFetcher;
        }

        @Override
        public CircularSet<URI> load(String serviceName) throws Exception {
            Set<URI> uris = serviceURIsFetcher.getInstances(serviceName);
            LOGGER.debug("ServiceInstancesCacheLoader load key = {}, uris = {}", serviceName, uris);
            return new CircularSet<URI>(uris);
        }
    }

}
