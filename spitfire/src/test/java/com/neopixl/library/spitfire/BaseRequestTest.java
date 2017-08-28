/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.neopixl.library.spitfire;

import com.android.volley.Cache;
import com.android.volley.ExecutorDelivery;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.neopixl.library.spitfire.listener.RequestListener;
import com.neopixl.library.spitfire.mock.DummyResponse;
import com.neopixl.library.spitfire.request.BaseRequest;
import com.neopixl.library.spitfire.utils.CacheTestUtils;
import com.neopixl.library.spitfire.utils.ImmediateResponseDelivery;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class BaseRequestTest {

    @Mock
    private RequestListener<DummyResponse> listener;
    @Mock
    private VolleyError volleyError;

    private String url = "http://neopixl.com/";
    private HashMap<String, String> parameters = new HashMap<>();
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> headersWithContentType = new HashMap<>();
    private DummyResponse dummyResponse = new DummyResponse();
    private DummyResponse dummyRequestObject = new DummyResponse();
    private ExecutorDelivery mDelivery;
    private Response<DummyResponse> mSuccessResponse;
    private Response<DummyResponse> mErrorResponse;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        parameters.put("page", "1");
        parameters.put("limit", "50");

        headers.put("If-Range", "Wed, 21 Oct 2017 07:28:00 GMT");
        headers.put("X-ApiKey", "azerty");
        headers.put("Authorization", "Bearer 1000:2b52d2ccfd6007d7a8d58d8cabb32bc0");

        headersWithContentType.putAll(headers);
        headersWithContentType.put("Content-Type", "application/json");

        byte[] data = new byte[16];
        Cache.Entry cacheEntry = CacheTestUtils.makeRandomCacheEntry(data);
        mDelivery = new ImmediateResponseDelivery();
        mSuccessResponse = Response.success(dummyResponse, cacheEntry);
        volleyError = new ServerError();
        mErrorResponse = Response.error(volleyError);
    }

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(BaseRequest.class.getMethod("getBodyContentType"));
        assertNotNull(BaseRequest.class.getMethod("getBody"));
        assertNotNull(BaseRequest.class.getMethod("getUrl"));
        assertNotNull(BaseRequest.class.getMethod("getJsonObject"));
        assertNotNull(BaseRequest.class.getMethod("getParams"));

        assertNotNull(BaseRequest.class.getDeclaredConstructor(BaseRequest.Builder.class));

        // Catch-all test to find API-breaking changes for the builder.
        assertNotNull(BaseRequest.Builder.class.getMethod("object",
                Object.class));
        assertNotNull(BaseRequest.Builder.class.getMethod("parameters",
                Map.class));
        assertNotNull(BaseRequest.Builder.class.getMethod("listener",
                RequestListener.class));
        assertNotNull(BaseRequest.Builder.class.getMethod("headers",
                Map.class));
        assertNotNull(BaseRequest.Builder.class.getMethod("build"));


        assertNotNull(BaseRequest.Builder.class.getConstructor(int.class, String.class, Class.class));
    }

    @Test
    public void builderGetGeneration() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.headers(headers);
        BaseRequest<DummyResponse> baseRequest = builder.build();

        String returnedUrl = baseRequest.getUrl();
        assertNotEquals("The url should not be the same", url, returnedUrl);

        for (String key : parameters.keySet()) {
            String value = parameters.get(key);
            assertTrue("The key should be contained", returnedUrl.contains(key +"="+ value));
        }

        assertEquals("The method should be the same", Request.Method.GET, baseRequest.getMethod());
        assertEquals("The parameters should be the same", parameters, baseRequest.getParams());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headers.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headers.get(key), returnedMap.get(key));
        }
    }

    @Test
    public void builderPostGeneration() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.object(dummyRequestObject);
        builder.headers(headers);

        BaseRequest<DummyResponse> baseRequest = builder.build();

        assertEquals("The url should be the same", url, baseRequest.getUrl());
        assertEquals("The method should be the same", Request.Method.POST, baseRequest.getMethod());
        assertEquals("The parameters should be the same", parameters, baseRequest.getParams());
        assertEquals("The object should be the same", dummyRequestObject, baseRequest.getJsonObject());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headers.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headers.get(key), returnedMap.get(key));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGenerationNoJsonInGet() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);
        builder.object(dummyRequestObject);

        BaseRequest<DummyResponse> baseRequest = builder.build();
        baseRequest.getJsonObject();
    }

    @Test
    public void builderPostGenerationWierdValue() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.headers(headersWithContentType);

        BaseRequest<DummyResponse> baseRequest = builder.build();

        assertEquals("The url should be the same", url, baseRequest.getUrl());
        assertEquals("The method should be the same", Request.Method.POST, baseRequest.getMethod());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headersWithContentType.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headersWithContentType.get(key), returnedMap.get(key));
        }
    }

    @Test
    public void listernerCall_error() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);
        builder.listener(listener);
        BaseRequest<DummyResponse> baseRequest = builder.build();

        mDelivery.postResponse(baseRequest, mErrorResponse);
        Mockito.verify(listener, Mockito.times(1)).onFailure(Mockito.eq(baseRequest), Mockito.isNull(NetworkResponse.class), Mockito.eq(volleyError));
    }

    @Test
    public void listernerCall_success() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);
        builder.listener(listener);
        BaseRequest<DummyResponse> baseRequest = builder.build();

        mDelivery.postResponse(baseRequest, mSuccessResponse);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(Mockito.eq(baseRequest), Mockito.isNull(NetworkResponse.class), Mockito.any(DummyResponse.class));
    }

    @Test
    public void requestPostConstruct_contentType() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.object(dummyRequestObject);
        builder.headers(headers);
        BaseRequest<DummyResponse> baseRequest = builder.build();

        assertTrue("The body should be json typed", baseRequest.getBodyContentType().contains("application/json"));
        assertNotNull(baseRequest.getBody());
    }

    @Test
    public void requestPostConstruct_body() throws Exception {
        BaseRequest<DummyResponse> baseRequest = Mockito.mock(BaseRequest.class);

        Mockito.when(baseRequest.getBody()).thenCallRealMethod();
        Mockito.when(baseRequest.getBodyContentType()).thenReturn("application/json; charset=null");
        Mockito.when(baseRequest.getJsonContentType()).thenCallRealMethod();
        Mockito.when(baseRequest.getMethod()).thenReturn(Request.Method.POST);

        baseRequest.getBody();
        Mockito.verify(baseRequest, Mockito.times(1)).getJsonBody();
    }

    @Test
    public void requestPostParamConstruct_contentType() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.headers(headers);
        BaseRequest<DummyResponse> baseRequest = builder.build();


        assertTrue("The body should not be json typed", baseRequest.getBodyContentType().contains("application/x-www-form-urlencoded"));
        assertNotNull(baseRequest.getBody());
    }

    @Test
    public void requestPostParamConstruct_body() throws Exception {
        BaseRequest<DummyResponse> baseRequest = Mockito.mock(BaseRequest.class);

        Mockito.when(baseRequest.getBody()).thenCallRealMethod();
        Mockito.when(baseRequest.getBodyContentType()).thenReturn("application/x-www-form-urlencoded; charset=null");
        Mockito.when(baseRequest.getMethod()).thenReturn(Request.Method.POST);

        baseRequest.getBody();
        Mockito.verify(baseRequest, Mockito.never()).getJsonBody();
    }

    @Test
    public void requestGetParamConstruct() throws Exception {
        BaseRequest.Builder<DummyResponse> builder = new BaseRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.headers(headers);
        BaseRequest<DummyResponse> baseRequest = builder.build();


        assertTrue("The body should not be json typed", baseRequest.getBodyContentType().contains("application/x-www-form-urlencoded"));
        assertNull(baseRequest.getBody());
    }
}
