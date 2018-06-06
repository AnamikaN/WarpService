package com.anamika.app.service;

import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static com.anamika.app.service.DefaultServiceURIsFetcher.DEFAULT_URI;
import static com.anamika.app.utils.Constants.FLEET_NET_SERVICE_NAME;
import static com.anamika.app.utils.Constants.HYPERDRIVE_SERVICE_NAME;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 *
 */
@Test
public class RoundRobinServiceInstanceProviderTest {
    private static final String NON_EXISTING_SERVICE = "non_existing_service";
    private final ServiceURIsFetcher fetcher = new DefaultServiceURIsFetcher();
    private final ServiceURIsFetcher mockFetcher = Mockito.mock(DefaultServiceURIsFetcher.class);

    @BeforeClass
    public void setUp(){
        when(mockFetcher.getInstances(anyString())).thenCallRealMethod();
    }

    @Test (expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Input service name cannot be null or empty!!")
    public void getInstanceServiceNameNull(){
        RoundRobinServiceInstanceProvider provider = new RoundRobinServiceInstanceProvider(fetcher, 5, TimeUnit.MINUTES);
        URI uri = provider.getInstance(null);
    }

    @Test (expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Input service name cannot be null or empty!!")
    public void getInstanceServiceNameEmpty(){
        RoundRobinServiceInstanceProvider provider = new RoundRobinServiceInstanceProvider(fetcher, 5, TimeUnit.MINUTES);
        URI uri = provider.getInstance("");
    }

    @Test
    public void getInstanceExistingService(){
        RoundRobinServiceInstanceProvider provider = new RoundRobinServiceInstanceProvider(fetcher, 5, TimeUnit.MINUTES);
        for (int i=0; i<3; ++i) {
            assertEquals(provider.getInstance(HYPERDRIVE_SERVICE_NAME), DEFAULT_URI);
        }
        for (int i=0; i<3; ++i) {
            assertEquals(provider.getInstance(FLEET_NET_SERVICE_NAME), DEFAULT_URI);
        }
        for (int i=0; i<3; ++i) {
            assertEquals(provider.getInstance(HYPERDRIVE_SERVICE_NAME), DEFAULT_URI);
            assertEquals(provider.getInstance(FLEET_NET_SERVICE_NAME), DEFAULT_URI);
        }
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "No instances available for service: " + NON_EXISTING_SERVICE)
    public void getInstanceNonExistingService(){
        RoundRobinServiceInstanceProvider provider = new RoundRobinServiceInstanceProvider(fetcher, 5, TimeUnit.MINUTES);
        URI uri = provider.getInstance(NON_EXISTING_SERVICE);
    }

    private void doUsage(final RoundRobinServiceInstanceProvider provider){
        for (int i=0; i<3; ++i) {
            assertEquals(provider.getInstance(HYPERDRIVE_SERVICE_NAME), DEFAULT_URI);
        }
        for (int i=0; i<3; ++i) {
            assertEquals(provider.getInstance(FLEET_NET_SERVICE_NAME), DEFAULT_URI);
        }
        for (int i=0; i<3; ++i) {
            assertEquals(provider.getInstance(HYPERDRIVE_SERVICE_NAME), DEFAULT_URI);
            assertEquals(provider.getInstance(FLEET_NET_SERVICE_NAME), DEFAULT_URI);
        }
    }
    @Test
    public void getInstanceExistingServiceWithCaching() throws InterruptedException {
        int instanceCacheDuration = 5;
        TimeUnit instanceCacheDurationTimeUnit = TimeUnit.SECONDS;
        RoundRobinServiceInstanceProvider provider = new RoundRobinServiceInstanceProvider(mockFetcher, instanceCacheDuration, instanceCacheDurationTimeUnit);

        doUsage(provider);
        verify(mockFetcher, times(1)).getInstances(HYPERDRIVE_SERVICE_NAME);
        verify(mockFetcher, times(1)).getInstances(FLEET_NET_SERVICE_NAME);

        //Sleep to expire the cache
        Thread.sleep(instanceCacheDurationTimeUnit.toMillis(instanceCacheDuration));

        doUsage(provider);
        verify(mockFetcher, times(2)).getInstances(HYPERDRIVE_SERVICE_NAME);
        verify(mockFetcher, times(2)).getInstances(FLEET_NET_SERVICE_NAME);

        doUsage(provider);
        verifyNoMoreInteractions(mockFetcher);
    }
}