package com.anamika.app.service;

import org.testng.annotations.Test;

import java.net.URI;
import java.util.Set;

import static com.anamika.app.service.DefaultServiceURIsFetcher.DEFAULT_URI;
import static com.anamika.app.utils.Constants.FLEET_NET_SERVICE_NAME;
import static com.anamika.app.utils.Constants.HYPERDRIVE_SERVICE_NAME;
import static org.testng.Assert.*;

/**
 *
 */
@Test
public class DefaultServiceURIsFetcherTest {
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Input Service Name cannot be null or empty!")
    public void testGetInstancesInputNull() {
        ServiceURIsFetcher test = new DefaultServiceURIsFetcher();
        Set<URI> uris = test.getInstances(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Input Service Name cannot be null or empty!")
    public void testGetInstancesInputEmpty() {
        ServiceURIsFetcher test = new DefaultServiceURIsFetcher();
        Set<URI> uris = test.getInstances("");
    }

    @Test
    public void testGetInstancesExistingServices() {
        ServiceURIsFetcher test = new DefaultServiceURIsFetcher();
        Set<URI> urisHyperdriveService = test.getInstances(HYPERDRIVE_SERVICE_NAME);
        assertNotNull(urisHyperdriveService);
        assertEquals(urisHyperdriveService.size(), 1);
        assertTrue(urisHyperdriveService.contains(DEFAULT_URI));

        Set<URI> urisFleetNetService = test.getInstances(FLEET_NET_SERVICE_NAME);
        assertNotNull(urisFleetNetService);
        assertEquals(urisFleetNetService.size(), 1);
        assertTrue(urisFleetNetService.contains(DEFAULT_URI));
    }

    @Test
    public void testGetInstancesNonExistingServices() {
        ServiceURIsFetcher test = new DefaultServiceURIsFetcher();
        Set<URI> uris = test.getInstances("non_existing_service");
        assertNotNull(uris);
        assertEquals(uris.size(), 0);
    }
}