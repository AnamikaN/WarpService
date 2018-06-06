package com.anamika.app.utils;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

@Test
public class CircularSetTest {
    @Test
    public void testNext(){
        List<URI> uris = Arrays.asList(
                URI.create("http://localhost:8081"),
                URI.create("http://localhost:8082"),
                URI.create("http://localhost:8083")
        );
        CircularSet<URI> circularSet = new CircularSet<>(ImmutableSet.<URI>builder().addAll(uris).build());
        for(int i=0; i <300; ++i){
            assertEquals(circularSet.next(), uris.get(i % uris.size()));
        }
    }
}