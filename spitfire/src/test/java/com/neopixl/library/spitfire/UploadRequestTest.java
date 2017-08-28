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
import com.neopixl.library.spitfire.request.UploadFileRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class UploadRequestTest {

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
}