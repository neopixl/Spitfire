package com.neopixl.spitfire;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neopixl.spitfire.listener.RequestListener;
import com.neopixl.spitfire.mock.DummyResponse;
import com.neopixl.spitfire.request.BaseRequest;
import com.neopixl.spitfire.utils.CacheTestUtils;
import com.neopixl.spitfire.utils.ImmediateResponseDelivery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.HttpURLConnection;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Florian ALONSO on 4/27/18.
 * For Neopixl
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
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
