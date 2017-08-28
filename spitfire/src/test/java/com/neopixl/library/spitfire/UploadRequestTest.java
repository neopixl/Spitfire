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
import com.neopixl.library.spitfire.model.RequestData;
import com.neopixl.library.spitfire.request.MultipartRequest;
import com.neopixl.library.spitfire.request.UploadFileRequest;
import com.neopixl.library.spitfire.utils.CacheTestUtils;
import com.neopixl.library.spitfire.utils.ImmediateResponseDelivery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class UploadRequestTest {

    @Mock
    private RequestListener<DummyResponse> listener;
    @Mock
    private VolleyError volleyError;

    private String url = "http://neopixl.com/";
    private HashMap<String, String> headers = new HashMap<>();
    private DummyResponse dummyResponse = new DummyResponse();
    private RequestData dummyData;
    private ExecutorDelivery mDelivery;
    private Response<DummyResponse> mSuccessResponse;
    private Response<DummyResponse> mErrorResponse;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        url = "http://neopixl.com/";

        headers.put("If-Range", "Wed, 21 Oct 2017 07:28:00 GMT");
        headers.put("X-ApiKey", "azerty");
        headers.put("Authorization", "Bearer 1000:2b52d2ccfd6007d7a8d58d8cabb32bc0");

        dummyData = new RequestData("neopixl.jpg", new byte[16]);

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
        assertNotNull(UploadFileRequest.class.getMethod("getBodyContentType"));
        assertNotNull(UploadFileRequest.class.getMethod("getBody"));
        assertNotNull(UploadFileRequest.class.getMethod("getPartData"));

        assertNotNull(UploadFileRequest.class.getDeclaredConstructor(UploadFileRequest.Builder.class));

        // Catch-all test to find API-breaking changes for the builder.
        assertNotNull(UploadFileRequest.Builder.class.getMethod("listener",
                RequestListener.class));
        assertNotNull(UploadFileRequest.Builder.class.getMethod("headers",
                Map.class));
        assertNotNull(UploadFileRequest.Builder.class.getMethod("partData",
                RequestData.class));
        assertNotNull(UploadFileRequest.Builder.class.getMethod("build"));

        assertNotNull(UploadFileRequest.Builder.class.getConstructor(int.class, String.class, Class.class));
    }

    @Test
    public void builderPostGeneration() throws Exception {
        UploadFileRequest.Builder<DummyResponse> builder = new UploadFileRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.headers(headers);
        builder.partData(dummyData);

        UploadFileRequest<DummyResponse> baseRequest = builder.build();

        assertEquals("The url should be the same", url, baseRequest.getUrl());
        assertEquals("The method should be the same", Request.Method.POST, baseRequest.getMethod());
        assertEquals("The multi part should be the same", dummyData, baseRequest.getPartData());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headers.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headers.get(key), returnedMap.get(key));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGenerationNoDataInGet() throws Exception {
        UploadFileRequest.Builder<DummyResponse> builder = new UploadFileRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);
        builder.partData(dummyData);

        UploadFileRequest<DummyResponse> baseRequest = builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGenerationNoData() throws Exception {
        UploadFileRequest.Builder<DummyResponse> builder = new UploadFileRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);

        UploadFileRequest<DummyResponse> baseRequest = builder.build();
    }

    @Test
    public void listernerCall_error() throws Exception {
        UploadFileRequest.Builder<DummyResponse> builder = new UploadFileRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.listener(listener);
        builder.partData(dummyData);
        UploadFileRequest<DummyResponse> baseRequest = builder.build();

        mDelivery.postResponse(baseRequest, mErrorResponse);
        Mockito.verify(listener, Mockito.times(1)).onFailure(Mockito.eq(baseRequest), Mockito.isNull(NetworkResponse.class), Mockito.eq(volleyError));
    }

    @Test
    public void listernerCall_success() throws Exception {
        UploadFileRequest.Builder<DummyResponse> builder = new UploadFileRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.listener(listener);
        builder.partData(dummyData);
        UploadFileRequest<DummyResponse> baseRequest = builder.build();

        mDelivery.postResponse(baseRequest, mSuccessResponse);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(Mockito.eq(baseRequest), Mockito.isNull(NetworkResponse.class), Mockito.any(DummyResponse.class));
    }

    @Test
    public void requestPostConstruct() throws Exception {
        UploadFileRequest.Builder<DummyResponse> builder = new UploadFileRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.headers(headers);
        builder.partData(dummyData);
        UploadFileRequest<DummyResponse> baseRequest = builder.build();

        assertEquals(dummyData.getType(), baseRequest.getBodyContentType());
        assertNotNull(baseRequest.getBody());
    }
}
