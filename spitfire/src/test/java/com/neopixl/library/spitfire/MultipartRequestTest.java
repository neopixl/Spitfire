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

import com.neopixl.library.spitfire.listener.RequestListener;
import com.neopixl.library.spitfire.model.RequestData;
import com.neopixl.library.spitfire.request.BaseRequest;
import com.neopixl.library.spitfire.request.MultipartRequest;
import com.neopixl.library.spitfire.request.UploadFileRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class MultipartRequestTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(MultipartRequest.class.getMethod("getBodyContentType"));
        assertNotNull(MultipartRequest.class.getMethod("getBody"));
        assertNotNull(MultipartRequest.class.getMethod("getUrl"));
        assertNotNull(MultipartRequest.class.getMethod("getJsonObjectBody"));
        assertNotNull(MultipartRequest.class.getMethod("getParams"));
        assertNotNull(MultipartRequest.class.getMethod("getMultiPartData"));

        assertNotNull(MultipartRequest.class.getDeclaredConstructor(MultipartRequest.Builder.class));

        // Catch-all test to find API-breaking changes for the builder.
        assertNotNull(MultipartRequest.Builder.class.getMethod("object",
                Object.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("parameters",
                Map.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("listener",
                RequestListener.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("headers",
                Map.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("multiPartData",
                HashMap.class));
        assertNotNull(MultipartRequest.Builder.class.getMethod("build"));

        assertNotNull(MultipartRequest.Builder.class.getConstructor(int.class, String.class, Class.class));
    }
}
