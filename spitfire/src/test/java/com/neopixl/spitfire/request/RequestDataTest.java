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

import com.neopixl.spitfire.model.RequestData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RequestDataTest {

    private String dummyFilename = "neopixl.jpg";
    private String dummyContentType = "image/jpeg";
    private byte[] dummyByte = new byte[16];

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(RequestData.class.getMethod("getFileName"));
        assertNotNull(RequestData.class.getMethod("getContent"));
        assertNotNull(RequestData.class.getMethod("getType"));

        assertNotNull(RequestData.class.getMethod("clear"));

        assertNotNull(RequestData.class.getMethod("setFileName", String.class));
        assertNotNull(RequestData.class.getMethod("setContent", byte[].class));
        assertNotNull(RequestData.class.getMethod("setType", String.class));

        assertNotNull(RequestData.class.getConstructor(String.class, byte[].class));

        assertNotNull(RequestData.class.getConstructor(String.class, byte[].class, String.class));
    }

    @Test
    public void emptyConstruction() throws Exception {
        RequestData requestData = new RequestData();

        assertNotNull(requestData.getContent());
        assertNotNull(requestData.getFileName());
        assertNull(requestData.getType());
    }

    @Test
    public void emptyConstructionThenFilled() throws Exception {
        RequestData requestData = new RequestData();
        requestData.setContent(dummyByte);
        requestData.setFileName(dummyFilename);
        requestData.setType(dummyContentType);

        assertEquals(dummyByte, requestData.getContent());
        assertEquals(dummyFilename, requestData.getFileName());
        assertEquals(dummyContentType, requestData.getType());
    }

    @Test
    public void fullConstruction() throws Exception {
        RequestData requestData = new RequestData(dummyFilename, dummyByte, dummyContentType);

        assertEquals(dummyByte, requestData.getContent());
        assertEquals(dummyFilename, requestData.getFileName());
        assertEquals(dummyContentType, requestData.getType());
    }

    @Test
    public void halfConstruction() throws Exception {
        RequestData requestData = new RequestData(dummyFilename, dummyByte);

        assertEquals(dummyByte, requestData.getContent());
        assertEquals(dummyFilename, requestData.getFileName());
        assertNull(requestData.getType());
    }

    @Test
    public void halfConstructionThenFilled() throws Exception {
        RequestData requestData = new RequestData(dummyFilename, dummyByte);
        requestData.setType(dummyContentType);

        assertEquals(dummyByte, requestData.getContent());
        assertEquals(dummyFilename, requestData.getFileName());
        assertEquals(dummyContentType, requestData.getType());
    }

    @Test
    public void fullConstructionThenClear() throws Exception {
        RequestData requestData = new RequestData(dummyFilename, dummyByte, dummyContentType);

        assertEquals(dummyByte, requestData.getContent());
        assertEquals(dummyFilename, requestData.getFileName());
        assertEquals(dummyContentType, requestData.getType());

        requestData.clear();

        assertEquals((new byte[0]).length, requestData.getContent().length);
        assertEquals(dummyFilename, requestData.getFileName());
        assertEquals(dummyContentType, requestData.getType());
    }
}
