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

import com.android.volley.NetworkResponse;
import com.neopixl.library.spitfire.listener.RequestListener;
import com.neopixl.library.spitfire.request.BaseRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class BaseRequestTest {

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        assertNotNull(BaseRequest.class.getMethod("getBodyContentType"));
        assertNotNull(BaseRequest.class.getMethod("getBody"));
        assertNotNull(BaseRequest.class.getMethod("getUrl"));
        assertNotNull(BaseRequest.class.getMethod("getJsonObjectBody"));
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
}
