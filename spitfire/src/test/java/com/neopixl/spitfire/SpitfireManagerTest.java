package com.neopixl.spitfire;

import com.android.volley.RetryPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Florian ALONSO on 4/27/18.
 * For Neopixl
 */
public class SpitfireManagerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(SpitfireManager.class.getMethod("getObjectMapper"));
        assertNotNull(SpitfireManager.class.getMethod("getDefaultRetryPolicy"));

        // Catch-all test to find API-breaking changes for the builder.
        assertNotNull(SpitfireManager.class.getMethod("setObjectMapper",
                ObjectMapper.class));
        assertNotNull(SpitfireManager.class.getMethod("setDefaultRetryPolicy",
                RetryPolicy.class));
        assertNotNull(SpitfireManager.class.getMethod("setRequestTimeout",
                int.class));
    }

    @Test
    public void objectMapperGetIsNotNull() throws Exception {
        assertNotNull(SpitfireManager.getObjectMapper());
    }

    @Test
    public void objectMapperGetIsNotAltered() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SpitfireManager.setObjectMapper(objectMapper);
        assertEquals("The reference should be the same", objectMapper, SpitfireManager.getObjectMapper());
    }

    @Test(expected = IllegalArgumentException.class)
    public void objectMapperNotNullTest() throws Exception {
        SpitfireManager.setObjectMapper(null);
    }
}
