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

package com.neopixl.spitfire.request;

import com.android.volley.Cache;
import com.android.volley.ExecutorDelivery;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.neopixl.spitfire.listener.RequestListener;
import com.neopixl.spitfire.mock.DummyResponse;
import com.neopixl.spitfire.model.RequestData;
import com.neopixl.spitfire.utils.CacheTestUtils;
import com.neopixl.spitfire.utils.ImmediateResponseDelivery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class MultipartRequestTest {

    @Mock
    private RequestListener<DummyResponse> listener;
    @Mock
    private VolleyError volleyError;

    private String url = "http://neopixl.com/";
    private HashMap<String, String> parameters = new HashMap<>();
    private HashMap<String, String> headers = new HashMap<>();
    private DummyResponse dummyResponse = new DummyResponse();
    private DummyResponse dummyRequestObject = new DummyResponse();
    private HashMap<String, List<RequestData>> dummyDataMap = new HashMap<>();
    private HashMap<String, RequestData> dummyDataMapNoList = new HashMap<>();
    private RequestData dummyData;
    private ExecutorDelivery mDelivery;
    private Response<DummyResponse> mSuccessResponse;
    private Response<DummyResponse> mErrorResponse;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        url = "http://neopixl.com/";

        parameters.put("page", "1");
        parameters.put("limit", "50");

        headers.put("If-Range", "Wed, 21 Oct 2017 07:28:00 GMT");
        headers.put("X-ApiKey", "azerty");
        headers.put("Authorization", "Bearer 1000:2b52d2ccfd6007d7a8d58d8cabb32bc0");

        dummyData = new RequestData("neopixl.jpg", new byte[16], "image/jpeg");
        List<RequestData> dataList = new ArrayList<>();
        dataList.add(dummyData);
        dummyDataMap.put("1", dataList);
        dataList = new ArrayList<>();
        dataList.add(dummyData);
        dummyDataMap.put("2", dataList);

        dummyDataMapNoList.put("1", dummyData);
        dummyDataMapNoList.put("2", dummyData);

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
        assertNotNull(MultipartRequest.class.getMethod("getBodyContentType"));
        assertNotNull(MultipartRequest.class.getMethod("getBody"));
        assertNotNull(MultipartRequest.class.getMethod("getUrl"));
        assertNotNull(MultipartRequest.class.getMethod("getJsonObject"));
        assertNotNull(MultipartRequest.class.getMethod("getParams"));
        assertNotNull(MultipartRequest.class.getMethod("getMultiPartData"));

        assertNotNull(MultipartRequest.class.getDeclaredConstructor(MultipartRequest.Builder.class));

        // Catch-all test to find API-breaking changes for the builder.
        assertNotNull(MultipartRequest.Builder.class.getMethod("object",
                Object.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("object",
                String.class, Object.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("parameters",
                Map.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("listener",
                RequestListener.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("headers",
                Map.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("multiPartData",
                HashMap.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("multiPartDataList",
                HashMap.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("insertMultiPartData",
                String.class, RequestData.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("build"));

        assertNotNull(MultipartRequest.Builder.class.getConstructor(int.class, String.class, Class.class));
    }

    @Test
    public void builderPostGeneration() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.object(dummyRequestObject);
        builder.headers(headers);
        builder.multiPartDataList(dummyDataMap);

        MultipartRequest<DummyResponse> baseRequest = builder.build();

        assertEquals("The url should be the same", url, baseRequest.getUrl());
        assertEquals("The method should be the same", Request.Method.POST, baseRequest.getMethod());
        assertEquals("The parameters should be the same", parameters, baseRequest.getParams());
        assertEquals("The object should be the same", dummyRequestObject, baseRequest.getJsonObject());
        assertEquals("The multi part should be the same", dummyDataMap, baseRequest.getMultiPartData());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headers.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headers.get(key), returnedMap.get(key));
        }
    }

    @Test
    public void builderPostGenerationWithPutMultipartSimple() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.object(dummyRequestObject);
        builder.headers(headers);
        builder.multiPartData(dummyDataMapNoList);

        MultipartRequest<DummyResponse> baseRequest = builder.build();

        assertEquals("The url should be the same", url, baseRequest.getUrl());
        assertEquals("The method should be the same", Request.Method.POST, baseRequest.getMethod());
        assertEquals("The parameters should be the same", parameters, baseRequest.getParams());
        assertEquals("The object should be the same", dummyRequestObject, baseRequest.getJsonObject());
        assertEquals("The multi part should be the same", dummyDataMap, baseRequest.getMultiPartData());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headers.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headers.get(key), returnedMap.get(key));
        }
    }

    @Test
    public void builderPostGenerationWithInsertMultipart() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.object(dummyRequestObject);
        builder.headers(headers);
        for (Map.Entry<String, List<RequestData>> entry : dummyDataMap.entrySet()) {
            for (RequestData datapart : entry.getValue()) {
                builder.insertMultiPartData(entry.getKey(), datapart);
            }
        }

        MultipartRequest<DummyResponse> baseRequest = builder.build();

        assertEquals("The url should be the same", url, baseRequest.getUrl());
        assertEquals("The method should be the same", Request.Method.POST, baseRequest.getMethod());
        assertEquals("The parameters should be the same", parameters, baseRequest.getParams());
        assertEquals("The object should be the same", dummyRequestObject, baseRequest.getJsonObject());
        assertEquals("The multi part should be the same", dummyDataMap, baseRequest.getMultiPartData());

        Map returnedMap = baseRequest.getHeaders();
        for (String key : headers.keySet()) {
            assertTrue("The key should be contained", returnedMap.containsKey(key));
            assertEquals("The same value should be contained", headers.get(key), returnedMap.get(key));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderGenerationNoMultipardInGet() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.GET, url, DummyResponse.class);

        MultipartRequest<DummyResponse> baseRequest = builder.build();
    }

    @Test
    public void listernerCall_error() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.listener(listener);
        MultipartRequest<DummyResponse> baseRequest = builder.build();

        mDelivery.postResponse(baseRequest, mErrorResponse);
        Mockito.verify(listener, Mockito.times(1)).onFailure(Mockito.eq(baseRequest), Mockito.isNull(NetworkResponse.class), Mockito.eq(volleyError));
    }

    @Test
    public void listernerCall_success() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.listener(listener);
        MultipartRequest<DummyResponse> baseRequest = builder.build();

        mDelivery.postResponse(baseRequest, mSuccessResponse);
        Mockito.verify(listener, Mockito.times(1)).onSuccess(Mockito.eq(baseRequest), Mockito.isNull(NetworkResponse.class), Mockito.any(DummyResponse.class));
    }

    @Test
    public void requestPostConstruct_contentType() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.object(dummyRequestObject);
        builder.headers(headers);
        builder.multiPartDataList(dummyDataMap);
        MultipartRequest<DummyResponse> baseRequest = builder.build();

        assertTrue(baseRequest.getBodyContentType().startsWith("multipart/form-data;boundary="));
        assertNotEquals("multipart/form-data;boundary=", baseRequest.getBodyContentType());
        assertNotNull(baseRequest.getBody());
    }

    @Test
    public void requestPostConstruct_body() throws Exception {
        MultipartRequest<DummyResponse> baseRequest = Mockito.mock(MultipartRequest.class);

        Mockito.when(baseRequest.getBody()).thenCallRealMethod();
        Mockito.when(baseRequest.calculateBody()).thenCallRealMethod();
        Mockito.when(baseRequest.getJsonObject()).thenReturn(dummyRequestObject);
        Mockito.when(baseRequest.getMultiPartData()).thenReturn(dummyDataMap);
        Mockito.when(baseRequest.getMethod()).thenReturn(Request.Method.POST);

        baseRequest.getBody();
        Mockito.verify(baseRequest, Mockito.times(1)).jsonParse(Mockito.any(DataOutputStream.class), Mockito.eq(dummyRequestObject));
        Mockito.verify(baseRequest, Mockito.times(1)).dataParse(Mockito.any(DataOutputStream.class), Mockito.eq(dummyDataMap));
    }

    @Test
    public void requestPostParamConstruct_contentType() throws Exception {
        MultipartRequest.Builder<DummyResponse> builder = new MultipartRequest.Builder<>(Request.Method.POST, url, DummyResponse.class);
        builder.parameters(parameters);
        builder.headers(headers);
        builder.multiPartDataList(dummyDataMap);
        MultipartRequest<DummyResponse> baseRequest = builder.build();


        assertTrue(baseRequest.getBodyContentType().startsWith("multipart/form-data;boundary="));
        assertNotEquals("multipart/form-data;boundary=", baseRequest.getBodyContentType());
        assertNotNull(baseRequest.getBody());
    }

    @Test
    public void requestPostParamConstruct_body() throws Exception {
        MultipartRequest<DummyResponse> baseRequest = Mockito.mock(MultipartRequest.class);

        Mockito.when(baseRequest.getBody()).thenCallRealMethod();
        Mockito.when(baseRequest.calculateBody()).thenCallRealMethod();
        Mockito.when(baseRequest.getParams()).thenReturn(parameters);
        Mockito.when(baseRequest.getMultiPartData()).thenReturn(dummyDataMap);
        Mockito.when(baseRequest.getMethod()).thenReturn(Request.Method.POST);

        baseRequest.getBody();
        Mockito.verify(baseRequest, Mockito.times(1)).textParse(Mockito.any(DataOutputStream.class), Mockito.eq(parameters), Mockito.isNull(String.class));
        Mockito.verify(baseRequest, Mockito.times(1)).dataParse(Mockito.any(DataOutputStream.class), Mockito.eq(dummyDataMap));

    }
}
